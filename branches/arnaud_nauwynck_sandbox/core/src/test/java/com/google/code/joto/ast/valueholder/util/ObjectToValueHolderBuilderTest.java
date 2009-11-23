package com.google.code.joto.ast.valueholder.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.testobj.A;
import com.google.code.joto.testobj.Pt;
import com.google.code.joto.testobj.SimpleIntFieldA;
import com.google.code.joto.testobj.SimpleRefBean;
import com.google.code.joto.testobj.SimpleRefObjectFieldA;
import com.google.code.joto.testobj.TestObjFactory;

public class ObjectToValueHolderBuilderTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(ObjectToValueHolderBuilderTest.class);
	private static boolean DEBUG = false;
	
	public ObjectToValueHolderBuilderTest(String name) {
		super(name);
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
			log.info(testName + " ...");
		}

		ObjectToValueHolderBuilder vhBuilder = new ObjectToValueHolderBuilder();
		AbstractObjectValueHolder vh = vhBuilder.buildValue(obj);
		assertNotNull(vh);
		assertSame(vh, vhBuilder.getResultMap().get(obj));
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
		PrintStream printStream = new PrintStream(bout); 
		ValueHolderPrettyPrinter printer = new ValueHolderPrettyPrinter(printStream);
		vh.visit(printer);
		
		if (DEBUG) {
			log.info("valueHolder={\n" + bout.toString() + "\n }");
		}
		
		if (DEBUG) {
			log.info("... done " + testName);
		}
	}

}
