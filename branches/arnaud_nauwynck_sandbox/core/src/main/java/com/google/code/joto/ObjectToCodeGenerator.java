package com.google.code.joto;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.ast.beanstmt.BeanAST;
import com.google.code.joto.ast.beanstmt.BeanAST.BeanStmt;
import com.google.code.joto.ast.beanstmt.impl.BeanASTPrettyPrinter;
import com.google.code.joto.ast.beanstmt.impl.BeanASTToStringFormatter;
import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.ast.valueholder.util.ObjectToValueHolderBuilder;
import com.google.code.joto.ast.valueholder.util.ValueHolderPrettyPrinter;
import com.google.code.joto.util.graph.DecoratorGraph;
import com.google.code.joto.util.graph.IGraph;
import com.google.code.joto.util.graph.TopologicalSort;
import com.google.code.joto.util.graph.helper.GraphPrinter;
import com.google.code.joto.value2java.Object2ASTInfo;
import com.google.code.joto.value2java.ValueHolderToBeanASTStmt;
import com.google.code.joto.value2java.VarDefUseDependencyGraphBuilder;

/**
 * main class for using Joto : Value to Java Stmt code generator
 */
public class ObjectToCodeGenerator {

	private static final Logger log = LoggerFactory.getLogger(ObjectToCodeGenerator.class);
	private boolean debug = false;
	private boolean debugValueHolder = false;
	private boolean debugLinksFromValueHolder = false;
	private boolean debugDependencyGraph = false;
	
	//-------------------------------------------------------------------------

	public ObjectToCodeGenerator() {
	}

	//-------------------------------------------------------------------------

	public void setDebug(boolean p) {
		this.debug = p;
	}
	
	public void setDebugValueHolder(boolean debugValueHolder) {
		this.debugValueHolder = debugValueHolder;
	}
	
	public void setDebugLinksFromValueHolder(boolean debugLinksFromValueHolder) {
		this.debugLinksFromValueHolder = debugLinksFromValueHolder;
	}

//	public void setDebugDependencyGraph(boolean debugDependencyGraph) {
//		this.debugDependencyGraph = debugDependencyGraph;
//	}

	/**
	 * 
	 * @param obj
	 * @param objName
	 * @return
	 */
	public String objToStmtsString(Object obj, String objName) {
		List<BeanStmt> stmts = objToStmts(obj, objName);

		ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
		PrintStream out = new PrintStream(bout);
		BeanASTPrettyPrinter stmtPrinter = new BeanASTPrettyPrinter(out); 
		for(BeanStmt stmt : stmts) {
			stmt.visit(stmtPrinter);
		}
		return bout.toString();
	}
	
	/**
	 * 
	 * @param obj
	 * @param objName
	 * @return
	 */
	public List<BeanStmt> objToStmts(Object obj, String objName) {
		ObjectToValueHolderBuilder b = new ObjectToValueHolderBuilder();
		b.buildValue(obj);
		
		Map<Object,AbstractObjectValueHolder> resMap = b.getResultMap();
		AbstractObjectValueHolder objVH = resMap.get(obj);

		if (debug && debugValueHolder) {
			log.info("ObjectToValueHolderBuilder => " + resMap.size() + " obj valueHolder(s)");
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			ValueHolderPrettyPrinter printer = new ValueHolderPrettyPrinter(new PrintStream(buffer)); 
			printer.setPrintLinksFrom(debugLinksFromValueHolder); 
			objVH.visit(printer);
			log.info(buffer.toString());
		}
		
		ValueHolderToBeanASTStmt b2jVisitor = new ValueHolderToBeanASTStmt();
		b2jVisitor.visitRootObject(objVH, objName);
		
		Map<AbstractObjectValueHolder, Object2ASTInfo> objInitInfos = 
			b2jVisitor.getResultObjInitInfoMap();
		
		List<BeanStmt> tmpUnsortedStmts = new ArrayList<BeanStmt>(); 
		for(Map.Entry<AbstractObjectValueHolder,Object2ASTInfo> e : objInitInfos.entrySet()) {
			Object2ASTInfo objInfo = e.getValue();
			tmpUnsortedStmts.add(objInfo.getVarDeclStmt());
			tmpUnsortedStmts.addAll(objInfo.getInitStmts());
		}
		
		if (b2jVisitor.getLogVarDeclStmt() != null) {
			tmpUnsortedStmts.add(b2jVisitor.getLogVarDeclStmt());
		}
		
		// build graph for topological sort
		IGraph<BeanAST> graph = new DecoratorGraph<BeanAST>();
		for(BeanStmt p : tmpUnsortedStmts) {
			graph.addVertex(p);
		}
		VarDefUseDependencyGraphBuilder graphBuilder =
			new VarDefUseDependencyGraphBuilder(graph);
		for(BeanStmt p : tmpUnsortedStmts) {
			p.visit(graphBuilder, p);
		}
	
		if (debug && debugDependencyGraph) {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
			GraphPrinter<BeanAST> graphPrinter = new GraphPrinter<BeanAST>(new PrintStream(buffer), BeanASTToStringFormatter.getInstance());
			graphPrinter.printGraph(graph);
			log.info("stmt dependency graph: {" + buffer.toString() + "} // stmt dependency graph");
		}
		
		// topological sort
		TopologicalSort<BeanAST> topologicalSort = new TopologicalSort<BeanAST>(graph);
		List<BeanAST> sortedASTs = topologicalSort.topologicalSort();
		List<BeanStmt> res = new ArrayList<BeanStmt>();
		for(BeanAST sortedAST : sortedASTs) {
			if (sortedAST instanceof BeanStmt) {
				res.add((BeanStmt) sortedAST);
			} else {
				// should not occur?!
				throw new IllegalStateException();
			}
		}
		
		return res;
	}
	
}
