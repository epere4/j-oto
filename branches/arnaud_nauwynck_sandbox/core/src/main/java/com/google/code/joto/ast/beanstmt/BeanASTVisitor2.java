package com.google.code.joto.ast.beanstmt;

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
 * Visitor design pattern, for BeanInitAST class hierarchy
 * 
 * this visitor variant use argument, and return value.. See also BeanInitVisitor without arg/return. 
 */
public interface BeanASTVisitor2<R,A> {

	R caseExprStmt(ExprStmt p, A arg);

	R caseMethodApplyExpr(MethodApplyExpr p, A arg);
	R caseLitteralExpr(LiteralExpr p, A arg);
	R caseNewObject(NewObjectExpr p, A arg);
	R caseNewArray(NewArrayExpr p, A arg);
	R caseClassExpr(ClassExpr p, A arg);
	R caseFieldExpr(FieldExpr p, A arg);
	R caseVarRef(VarRefExpr p, A arg);

	R caseVarDecl(VarDeclStmt p, A arg);
	R caseBlock(BlockStmt p, A arg);

}
