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
 * See also BeanInitVisitor2 with arg/return. 
 */
public interface BeanASTVisitor {

	void caseExprStmt(ExprStmt exprStmt);

	void caseMethodApplyExpr(MethodApplyExpr p);
	void caseLitteralExpr(LiteralExpr p);
	void caseNewObject(NewObjectExpr p);
	void caseNewArray(NewArrayExpr p);
	void caseClassExpr(ClassExpr p);
	void caseFieldExpr(FieldExpr p);
	void caseVarRef(VarRefExpr p);

	void caseVarDecl(VarDeclStmt stmt);
	void caseBlock(BlockStmt stmt);

}
