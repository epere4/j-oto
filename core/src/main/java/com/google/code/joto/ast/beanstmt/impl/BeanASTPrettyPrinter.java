package com.google.code.joto.ast.beanstmt.impl;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import com.google.code.joto.ast.beanstmt.BeanASTVisitor;
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

/**
 * 
 */
public class BeanASTPrettyPrinter implements BeanASTVisitor {


	private PrintStream out;
	
	private int indent;
	
	// ------------------------------------------------------------------------
	
	public BeanASTPrettyPrinter(PrintStream out) {
		this.out = out;
	}

	// implements BeanInitVisitor Statements
	// ------------------------------------------------------------------------
	
	public void caseBlock(BlockStmt p) {
		indentPrintln("{");
		incrIndent();
		for(BeanStmt stmt : p.getStmts()) {
			stmt.visit(this);
		}
		decrIndent();
		indentPrintln("}");
	}


	public void caseVarDecl(VarDeclStmt stmt) {
		Class<?> declType = stmt.getDeclaredType();
		String simpleClassName = classToSimpleName(declType); 
		indentPrint(simpleClassName + " " + stmt.getVarName());
		if (stmt.getInitExpr() != null) {
			print(" = ");
			stmt.getInitExpr().visit(this);
		}
		println(";");
	}

	public void caseExprStmt(ExprStmt exprStmt) {
		printIndent();
		exprStmt.getExpr().visit(this);
		println(";");
	}

	// implements BeanInitVisitor Expressions
	// ------------------------------------------------------------------------
	

	public void caseMethodApplyExpr(MethodApplyExpr p) {
		if (p.getLhsExpr() != null) {
			p.getLhsExpr().visit(this);
			print(".");
		} else {
			int dbg = 0; dbg++;
		}
		print(p.getMethodName());
		print("(");
		visitExprList(p.getArgs());
		print(")");
	}
	
	public void caseNewObject(NewObjectExpr p) {
		print("new ");
		print(classToSimpleName(p.getNewClss()));
		print("(");
		visitExprList(p.getArgs());
		print(")");
	}

	public void caseNewArray(NewArrayExpr p) {
		print("new ");
		print(classToSimpleName(p.getNewArrayClass()));
		print("[");
		print(Integer.toString(p.getLength()));
		print("]");
	}
	
	public void caseLitteralExpr(LiteralExpr p) {
		String javaValue;
		Object value = p.getValue();
		if (value == null) {
			javaValue = "null";
		} else if (value instanceof String) {
			String str = (String) value;
			str = str.replaceAll("\"", "\\\"");
			// TODO add more escape..
			javaValue = "\"" + str + "\""; 
		} else {
			javaValue = value.toString(); // TODO format...
		}
		print(javaValue);
	}


	public void caseClassExpr(ClassExpr p) {
		throw new UnsupportedOperationException("TODO");
	}


	public void caseFieldExpr(FieldExpr p) {
		throw new UnsupportedOperationException("TODO");
	}
	
	public void caseVarRef(VarRefExpr p) {
		print(p.getVarName());
	}

	// ------------------------------------------------------------------------

	public void visitStmtList(List<BeanStmt> stmts) {
		for(BeanStmt stmt : stmts) {
			stmt.visit(this);
		}
	}
	
	protected void visitExprList(List<BeanExpr> args) {
		if (args != null && !args.isEmpty()) {
			for (Iterator<BeanExpr> iter = args.iterator(); iter.hasNext();) {
				BeanExpr arg = iter.next();
				arg.visit(this);
				if (iter.hasNext()) {
					print(", ");
				}
			}
		}
	}
	
	protected String classToSimpleName(Class<?> p) {
		if (p == null) {
			p = Object.class; // should not occur!
		}
		String res = p.getSimpleName();
		// TODO add import..
		if (-1 != res.indexOf('$')) {
			res = res.replace('$', '.'); // TODO add static inner class import?
		}
		return res;
	}
	
	// -------------------------------------------------------------------------
	
	public void incrIndent() {
		indent++;
	}

	public void decrIndent() {
		indent--;
	}
	public void printIndent() {
		for (int i = 0; i < indent; i++) {
			out.print(' ');
		}
	}

	private void print(String text) {
		out.print(text);
	}

	private void println() {
		out.print('\n');
	}

	private void println(String text) {
		print(text);
		println();
	}

	private void indentPrint(String text) {
		printIndent();
		print(text);
	}
	
	private void indentPrintln(String text) {
		printIndent();
		print(text);
		println();
	}

}