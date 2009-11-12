package com.google.code.joto.ast.beanstmt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.code.joto.ast.beanstmt.impl.BeanASTToStringFormatter;
import com.google.code.joto.util.attr.DefaultAttributeSupport;
import com.google.code.joto.util.attr.IAttributeSupport;
import com.google.code.joto.util.attr.IAttributeSupportDelegate;

public abstract class BeanAST implements IAttributeSupportDelegate {

	private IAttributeSupport attributeSupport;
	

	public BeanAST() {
	}
	
	public abstract void visit(BeanASTVisitor v);
	public abstract <R,A> R visit(BeanASTVisitor2<R,A> v, A arg);

	public IAttributeSupport getAttributeSupport() {
		 if (attributeSupport == null) {
			 attributeSupport = new DefaultAttributeSupport();
		}
		 return attributeSupport;
	}

	@Override
	public String toString() {
		String javaText = BeanASTToStringFormatter.getInstance().objectToString(this);
		return javaText;
	}
	
	// -------------------------------------------------------------------------
	


	/**
	 *
	 */
	public static abstract class BeanStmt extends BeanAST {
		
	}

	/**
	 *
	 */
	public static abstract class BeanExpr extends BeanAST {
		
	}
	
	/**
	 *
	 */
	public static class ExprStmt extends BeanStmt {
		private BeanExpr beanExpr;

		public ExprStmt(BeanExpr beanExpr) {
			this.beanExpr = beanExpr;
		}

		public void visit(BeanASTVisitor v) {
			v.caseExprStmt(this);
		}
		
		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseExprStmt(this, arg);
		}

		public BeanExpr getExpr() {
			return beanExpr;
		}
		
	}
	
	/**
	 * 
	 */
	public static class MethodApplyExpr extends BeanExpr {
		BeanExpr lhsExpr;
		String methodName;
		List<BeanExpr> args = new ArrayList<BeanExpr>();

		
		public MethodApplyExpr(BeanExpr lhsExpr, String methodName, List<BeanExpr> args) {
			this.lhsExpr = lhsExpr;
			this.methodName = methodName;
			this.args = args;
		}

		public MethodApplyExpr(BeanExpr lhsExpr, String methodName, BeanExpr... optArgs) {
			this.lhsExpr = lhsExpr;
			this.methodName = methodName;
			if (optArgs != null) {
				args.addAll(Arrays.asList(optArgs));
			}
		}

		public void visit(BeanASTVisitor v) {
			v.caseMethodApplyExpr(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseMethodApplyExpr(this, arg);
		}

		public BeanExpr getLhsExpr() {
			return lhsExpr;
		}

		public String getMethodName() {
			return methodName;
		}

		public List<BeanExpr> getArgs() {
			return args;
		}

	}
	
	/**
	 * 
	 */
	public static class LiteralExpr extends BeanExpr {
		Object value;

		public LiteralExpr(Object value) {
			super();
			this.value = value;
		}

		public void visit(BeanASTVisitor v) {
			v.caseLitteralExpr(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseLitteralExpr(this, arg);
		}

		public Object getValue() {
			return value;
		}
		
	}

	/**
	 * 
	 */
	public static class NewObjectExpr extends BeanExpr {
		Class newClss;
		List<BeanExpr> args = new ArrayList<BeanExpr>();
		
		public NewObjectExpr(Class clss) {
			this(clss, (List) null);
		}

		public NewObjectExpr(Class clss, BeanExpr... args) {
			this(clss, Arrays.asList(args));
		}
		
		public NewObjectExpr(Class clss, List<BeanExpr> args) {
			super();
			this.newClss = clss;
			if (args != null) {
				this.args.addAll(args);
			}
		}

		public void visit(BeanASTVisitor v) {
			v.caseNewObject(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseNewObject(this, arg);
		}

		public Class getNewClss() {
			return newClss;
		}

		public List<BeanExpr> getArgs() {
			return args;
		}
		
	}

	/**
	 * 
	 */
	public static class NewArrayExpr extends BeanExpr {
		private Class newArrayClass;
		private int length;

		
		public NewArrayExpr(Class newArrayClass, int length) {
			super();
			this.newArrayClass = newArrayClass;
			this.length = length;
		}

		public void visit(BeanASTVisitor v) {
			v.caseNewArray(this);
		}
		
		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseNewArray(this, arg);
		}

		public Class getNewArrayClass() {
			return newArrayClass;
		}

		public int getLength() {
			return length;
		}
		
	}
	
	/**
	 * 
	 */
	public static class ClassExpr extends BeanExpr {
		Class valueClss;

		public ClassExpr(Class valueClss) {
			super();
			this.valueClss = valueClss;
		}

		public void visit(BeanASTVisitor v) {
			v.caseClassExpr(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseClassExpr(this, arg);
		}

		public Class getValueClss() {
			return valueClss;
		}
		
	}

	/**
	 * 
	 */
	public static class FieldExpr extends BeanExpr {
		Field field;
		
		public FieldExpr(Field field) {
			super();
			this.field = field;
		}

		public void visit(BeanASTVisitor v) {
			v.caseFieldExpr(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseFieldExpr(this, arg);
		}

		public Field getField() {
			return field;
		}
		
	}

	/**
	 * 
	 */
	public static class VarRefExpr extends BeanExpr {
		String varName;
		VarDeclStmt resolvedDecl;
		
		public VarRefExpr(String p) {
			super();
			this.varName = p;
		}

		public VarRefExpr(VarDeclStmt resolvedDecl) {
			super();
			this.resolvedDecl = resolvedDecl;
			this.varName = resolvedDecl.getVarName();
		}

		public void visit(BeanASTVisitor v) {
			v.caseVarRef(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseVarRef(this, arg);
		}

		public String getVarName() {
			return varName;
		}

		public VarDeclStmt getResolvedDecl() {
			return resolvedDecl;
		}
		
	}

	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class VarDeclStmt extends BeanStmt {
		
		private Class<?> declaredType;
		private String varName;
		private BeanExpr initExpr; 
		
		public VarDeclStmt(Class<?> declaredType, String varName, BeanExpr initExpr) {
			this.declaredType = declaredType;
			this.varName = varName;
			this.initExpr = initExpr;
		}
		
		public void visit(BeanASTVisitor v) {
			v.caseVarDecl(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseVarDecl(this, arg);
		}

		public Class<?> getDeclaredType() {
			return declaredType;
		}

		public void setDeclaredType(Class<?> declaredType) {
			this.declaredType = declaredType;
		}

		public String getVarName() {
			return varName;
		}

		public void setVarName(String p) {
			this.varName = p;
		}

		public BeanExpr getInitExpr() {
			return initExpr;
		}

		public void setInitExpr(BeanExpr p) {
			this.initExpr = p;
		}
		
	}
	
	/**
	 * 
	 */
	public static class BlockStmt extends BeanStmt {
		
		private List<BeanStmt> stmts = new ArrayList<BeanStmt>();
		
		public BlockStmt() {
		}
		
		public void visit(BeanASTVisitor v) {
			v.caseBlock(this);
		}
		
		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseBlock(this, arg);
		}
		
		public void doVisitChildStmts(BeanASTVisitor v) {
			for(BeanStmt stmt : stmts) {
				stmt.visit(v);
			}
		}
		public <R,A> R doVisitChildStmts(BeanASTVisitor2<R,A> v, A arg) {
			for(BeanStmt stmt : stmts) {
				stmt.visit(v, arg);
			}
			return null;
		}
		
		public List<BeanStmt> getStmts() {
			return stmts;
		}

		public void addStmt(BeanStmt p) {
			stmts.add(p);
		}
		
		public void addExprStmt(BeanExpr p) {
			addStmt(new ExprStmt(p));
		}
		
	}
	
}
