package com.google.code.joto.eventrecorderui;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Date;

import javax.swing.JFrame;

import com.google.code.joto.eventrecorder.RecordEventHandle;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.eventrecorderui.RecordEventPanel;
import com.google.code.joto.testobj.Pt;
import com.google.code.joto.testobj.TestObjFactory;

public class UiTestMain {

	public static void main(String[] args) {
		
		RecordEventStore eventStore = new DefaultMemoryRecordEventStore();
		
		doRecordEventObj(eventStore, "SimpleIntFieldA", TestObjFactory.createSimpleIntFieldA());
		// doRecordEventObj(eventStore, "SimpleRefObjectFieldA", TestObjFactory.createSimpleRefObjectFieldA());
		doRecordEventObj(eventStore, "A", TestObjFactory.createBeanA());
		doRecordEventObj(eventStore, "A2", TestObjFactory.createBeanA2());
		doRecordEventObj(eventStore, "SimpleRefBean_Cyclic", TestObjFactory.createSimpleRefBean_Cyclic());
		doRecordEventObj(eventStore, "Pt", new Pt(1, 2));

		RecordEventPanel recordEventPanel = new RecordEventPanel(eventStore);
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(recordEventPanel.getJComponent());
		frame.pack();
		frame.setVisible(true);
		
	}

	private static void doRecordEventObj(RecordEventStore eventStore,
			String methodName, Serializable objData) {
		
		RecordEventHandle evt = new RecordEventHandle(-1);
		evt.setEventDate(new Date());
		evt.setEventType("testEventType");
		evt.setEventSubType("testEventSubType"); 
		evt.setEventMethodName(methodName);

		try {
			eventStore.addEvent(evt, objData);
		} catch(Exception ex) {
			System.err.println("Failed to serialize?.. ignore");
		}
	}
}
