package com.google.code.joto;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import junit.framework.TestCase;

import com.google.code.joto.ast.beanstmt.BeanAST.BeanStmt;
import com.google.code.joto.ast.beanstmt.impl.BeanASTPrettyPrinter;
import com.google.code.joto.testobj.A;
import com.google.code.joto.testobj.Pt;
import com.google.code.joto.testobj.SimpleIntFieldA;
import com.google.code.joto.testobj.SimpleRefBean;
import com.google.code.joto.testobj.SimpleRefObjectFieldA;
import com.google.code.joto.testobj.TestObjFactory;


/**
 * JUnit test for ObjectToCodeGenerator 
 */
public class AppTest extends TestCase {

	private static boolean DEBUG = false;
	
	public AppTest(String testName) {
		super(testName);
    }

	public void test_SimpleIntFieldA() {
		SimpleIntFieldA a = TestObjFactory.createSimpleIntFieldA();
		doTest("test_SimpleIntFieldA", a);
	}

	public void test_SimpleRefObjectFieldA() {
		SimpleRefObjectFieldA a = TestObjFactory.createSimpleRefObjectFieldA();
		doTest("test_SimpleRefObjectFieldA", a);
	}

	public void test_A() {
		A a = TestObjFactory.createBeanA();
		doTest("test_A", a);
	}

	public void test_A2() {
		A a = TestObjFactory.createBeanA2();
		doTest("test_A2", a);
	}

	public void test_SimpleRefA() {
		Object a = TestObjFactory.createSimpleRefA();
		doTest("test_SimpleRefA", a);
	}

	public void test_SimpleRefBeanCyclic() {
		SimpleRefBean a = TestObjFactory.createSimpleRefBean_Cyclic();
		doTest("test_SimpleRefBeanCyclic", a);
	}

	public void test_Pt() {
		Object a = new Pt(1, 2);
		doTest("test_Pt", a);
	}

	private void doTest(String testName, Object obj) {
		if (DEBUG) {
			System.out.println("\n" + testName + " ...");
		}
		
		ObjectToCodeGenerator v2j = new ObjectToCodeGenerator();
		// v2j.setDebug(DEBUG);
		// v2j.setDebugValueHolder(DEBUG);
		// v2j.setDebugLinksFromValueHolder(DEBUG);

		List<BeanStmt> stmts = v2j.objToStmts(obj, "a");
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
		PrintStream printStream = new PrintStream(bout); 
		BeanASTPrettyPrinter stmtPrinter = new BeanASTPrettyPrinter(printStream); 
		stmtPrinter.visitStmtList(stmts);
		if (DEBUG) {
			System.out.println("code={\n" + bout.toString() + "\n }");
		}
		
		if (DEBUG) {
			System.out.println("... done " + testName + "\n");
		}
	}


}
