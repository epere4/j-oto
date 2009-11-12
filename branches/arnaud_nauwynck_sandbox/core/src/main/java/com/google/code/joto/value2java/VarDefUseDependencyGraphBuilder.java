package com.google.code.joto.value2java;

import java.util.List;

import com.google.code.joto.ast.beanstmt.BeanAST;
import com.google.code.joto.ast.beanstmt.BeanASTVisitor2;
import com.google.code.joto.ast.beanstmt.BeanAST.BeanExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.BeanStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.BlockStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.ClassExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.ExprStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.FieldExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.LiteralExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.MethodApplyExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.NewArrayExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.NewObjectExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.VarDeclStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.VarRefExpr;
import com.google.code.joto.util.graph.IGraph;


public class VarDefUseDependencyGraphBuilder implements BeanASTVisitor2<Object,BeanAST> {

	private IGraph<BeanAST> graph; 
	
	// -------------------------------------------------------------------------
	
	public VarDefUseDependencyGraphBuilder(IGraph<BeanAST> graph) {
		this.graph = graph;
	}
	
	// -------------------------------------------------------------------------

	
	protected void addUseDef(BeanAST use, BeanAST def) {
		graph.addLink(def, use);
//		graph.addLink(use, def); //????
	}
	
	// -------------------------------------------------------------------------

	public Object caseVarRef(VarRefExpr p, BeanAST parent) {
		addUseDef(parent, p.getResolvedDecl());
		return null;
	}

	public Object caseVarDecl(VarDeclStmt p, BeanAST parent) {
		// do nothing
		BeanExpr initExpr = p.getInitExpr();
		if (initExpr != null) {
			initExpr.visit(this, p);
		}
		return null;
	}

	public Object caseBlock(BlockStmt p, BeanAST parent) {
		for(BeanStmt stmt : p.getStmts()) {
			stmt.visit(this, p); // p replace parent... when block can not be split ??
		}
		return null;
	}

	public Object caseExprStmt(ExprStmt p, BeanAST parent) {
		p.getExpr().visit(this, parent);
		return null;
	}

	public Object caseClassExpr(ClassExpr p, BeanAST parent) {
		// do nothing
		return null;
	}

	public Object caseFieldExpr(FieldExpr p, BeanAST parent) {
		// do nothing			
		return null;
	}

	public Object caseLitteralExpr(LiteralExpr p, BeanAST parent) {
		// do nothing
		return null;
	}

	public Object caseMethodApplyExpr(MethodApplyExpr p, BeanAST parent) {
		BeanExpr lhs = p.getLhsExpr();
		if (lhs != null) {
			lhs.visit(this, parent);
		}
		doVisitLsExpr(p.getArgs(), parent);
		return null;
	}

	public Object caseNewArray(NewArrayExpr p, BeanAST parent) {
		// do nothing
		return null;
	}

	public Object caseNewObject(NewObjectExpr p, BeanAST parent) {
		doVisitLsExpr(p.getArgs(), parent);
		return null;
	}

	// -------------------------------------------------------------------------
	
	protected void doVisitLsExpr(List<BeanExpr> ls, BeanAST parent) {
		for(BeanExpr elt : ls) {
			elt.visit(this, parent);
		}
	}
	
}
