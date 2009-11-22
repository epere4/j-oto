package com.google.code.joto.eventrecorder.ui;

import java.io.Serializable;
import java.util.Date;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.calls.DefaultSerializableFoo;
import com.google.code.joto.eventrecorder.calls.IFoo;
import com.google.code.joto.eventrecorder.calls.MethodEventGeneratorInvocationHandlerTest;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.testobj.Pt;
import com.google.code.joto.testobj.TestObjFactory;

public class UiTestMain {

	private static Logger log = LoggerFactory.getLogger(UiTestMain.class);
	
	public static void main(String[] args) {
		
		RecordEventStore eventStore = new DefaultMemoryRecordEventStore();
		
		// record Serializable POJO
		doRecordEventObj(eventStore, "SimpleIntFieldA", TestObjFactory.createSimpleIntFieldA());
		// doRecordEventObj(eventStore, "SimpleRefObjectFieldA", TestObjFactory.createSimpleRefObjectFieldA());
		doRecordEventObj(eventStore, "A", TestObjFactory.createBeanA());
		doRecordEventObj(eventStore, "A2", TestObjFactory.createBeanA2());
		doRecordEventObj(eventStore, "SimpleRefBean_Cyclic", TestObjFactory.createSimpleRefBean_Cyclic());
		doRecordEventObj(eventStore, "Pt", new Pt(1, 2));

		// also record method calls using java.lang.reflect.Proxy + interface
		{
			IFoo fooImpl = new DefaultSerializableFoo();
			IFoo fooProxy =  MethodEventGeneratorInvocationHandlerTest.createFooProxyRecorder(fooImpl, eventStore);
			
			MethodEventGeneratorInvocationHandlerTest.doCallFooMethods(fooProxy);
		}
		
		
		
		RecordEventPanel recordEventPanel = new RecordEventPanel(eventStore);
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(recordEventPanel.getJComponent());
		frame.pack();
		frame.setVisible(true);
		
	}

	private static void doRecordEventObj(RecordEventStore eventStore,
			String methodName, Serializable objData) {
		
		RecordEventSummary evt = new RecordEventSummary(-1);
		evt.setEventDate(new Date());
		evt.setEventType("testObj");
		evt.setEventSubType("testObj SubType"); 
		evt.setEventMethodName(methodName);

		try {
			eventStore.addEvent(evt, objData);
		} catch(Exception ex) {
			log.warn("Failed to serialize?.. ignore", ex);
		}
	}
}
