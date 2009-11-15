package com.google.code.joto.eventrecorder.calls;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.RecordEventStoreGenerator;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.testobj.SerializableObj;

/**
 * JUnit test for MethodEventGeneratorInvocationHandler
 *
 */
public class MethodEventGeneratorInvocationHandlerTest extends TestCase {

	private static class TestData {
		IFoo impl;
		DefaultMemoryRecordEventStore eventStore;
		RecordEventStoreGenerator eventGen;
		IFoo fooProxy;
		
		public TestData() {
			impl = new DefaultFoo();
			
			eventStore = new DefaultMemoryRecordEventStore();
			eventGen = new RecordEventStoreGenerator(eventStore); 
			
			ClassLoader classLoader = getClass().getClassLoader();
			InvocationHandler h = 
				new MethodEventGeneratorInvocationHandler(impl, eventGen, "foo proxy", "request", "response");
			fooProxy = (IFoo) Proxy.newProxyInstance(classLoader, new Class[] { IFoo.class }, h );
		}
	}
	
	public MethodEventGeneratorInvocationHandlerTest(String name) {
		super(name);
	}

	public void testFooSimple() {
		TestData d = new TestData();
		IFoo fooProxy = d.fooProxy;
		
		String res = fooProxy.methSimple("test", 1);
		assertEquals("test1", res);
		
		List<RecordEventSummary> events = d.eventStore.getEvents();
		assertEquals(2, events.size());
		
		RecordEventSummary eventRequest = events.get(0);
		RecordEventData eventRequestData = d.eventStore.getEventData(eventRequest);
		assertNotNull(eventRequestData);
		EventMethodRequestData requestData = (EventMethodRequestData) 
			eventRequestData.getObjectDataCopy();
		assertNotNull(requestData);
		assertTrue(requestData.getExpr() != d.impl && requestData.getExpr().getClass() == d.impl.getClass()); // serialied obj => different instance!
		Method methodSimple;
		try {
			methodSimple = IFoo.class.getMethod("methSimple", new Class[] { String.class, int.class} );
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		// assertSame(methodSimple, requestData.getMethod()); //wrong!
		assertEquals(methodSimple, requestData.getMethod());
		assertEquals(2, requestData.getArguments().length);
		assertEquals("test", requestData.getArguments()[0]);
		assertEquals(Integer.valueOf(1), requestData.getArguments()[1]);
		
		RecordEventSummary eventResponse = events.get(1);
		RecordEventData eventResponseData = d.eventStore.getEventData(eventResponse);
		assertNotNull(eventResponseData);
		EventMethodResponseData responseData = (EventMethodResponseData) 
			eventResponseData.getObjectDataCopy();
		assertNotNull(responseData);
		assertEquals(eventRequest.getEventId(), responseData.getRequestEventId());
		assertEquals("test1", responseData.getResult());
		assertNull(responseData.getException());
		
		// call more..
		fooProxy.methSimple("test", 1);
		assertEquals(4, d.eventStore.getEvents().size());
	}
	
	public void testAllFooMethods() {
		TestData d = new TestData();
		IFoo fooProxy = d.fooProxy;
		
		fooProxy.methVoid(1);
		fooProxy.methVoidFromPrimitives(1, 1.0f, 1.0, 'a', true, (byte)0);
		fooProxy.methVoidFromPrimitivesArrays(new int[] { 1 }, new float[] { 1.0f }, new double[]{ 1.0 }, new char[] { 'a' }, new boolean[] { true }, new byte[] { (byte)1 });
		fooProxy.methVoidFromObj(new SerializableObj(), "a", new Date(), Integer.valueOf(1), new Float(1.0f), new Double(1.0));
		fooProxy.methVoidFromObjArrays(new Object[] { new SerializableObj() }, new String[] { "a" }, new Date[] { new Date() } , new Integer[] {new Integer(1)}, new Float[] { new Float(1.0f) } , new Double[] { new Double(1.0) } );
		
		fooProxy.methInt();
		fooProxy.methFloat();
		fooProxy.methDouble();

		fooProxy.methObj();
		fooProxy.methString();
		
		fooProxy.methObjArray();
		fooProxy.methIntArray();
		fooProxy.methFloatArray();
		fooProxy.methStringArray();
		
	}
}
