package com.google.code.joto.eventrecorder.ui;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventStoreGenerator;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.calls.DefaultSerializableFoo;
import com.google.code.joto.eventrecorder.calls.IFoo;
import com.google.code.joto.eventrecorder.calls.MethodEventGeneratorInvocationHandlerTest;
import com.google.code.joto.eventrecorder.ext.log.EventStoreGeneratorLog4jAppender;
import com.google.code.joto.eventrecorder.ext.log.EventStoreGeneratorLogbackAppender;
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
		
		{ // record events using Logback event generator
			String eventType = "logback";
			String loggerName = "a.b.Test";
			RecordEventStoreGenerator eventGenerator = new RecordEventStoreGenerator(eventStore);

			LoggerContext loggerContext = new LoggerContext();
			loggerContext.reset();
			Logger logger = loggerContext.getLogger(loggerName);

			EventStoreGeneratorLogbackAppender eventAppender = 
				new EventStoreGeneratorLogbackAppender(eventGenerator, eventType);
			eventAppender.start();
			((ch.qos.logback.classic.Logger) logger).addAppender(eventAppender);
			
			// now test logging events from log4j
			logger.info("test info message");
			logger.warn("test warn message");
			logger.warn("test warn message with ex", new Exception());
			logger.error("test error message");
			logger.info("test info message multiline\n... message line 2\n...message line 3");

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
			cal.set(1999, 12, 31, 23, 59, 59);
			logger.info("test info message with arguments: str={} int={} date={}", new Object[] { "test", 123, cal.getTime() });
		}

		{ // record events using deprecated log4j event generator
			String eventType = "log4j";
			String loggerName = "a.b.Test";
			RecordEventStoreGenerator eventGenerator = new RecordEventStoreGenerator(eventStore);

			org.apache.log4j.Logger logger = 
				org.apache.log4j.Logger.getLogger(loggerName);

			EventStoreGeneratorLog4jAppender eventAppender = 
				new EventStoreGeneratorLog4jAppender(eventGenerator, eventType);
			((org.apache.log4j.Logger) logger).addAppender(eventAppender);
			
			// now test logging events from log4j
			logger.info("test info message");
			logger.warn("test warn message");
			logger.warn("test warn message with ex", new Exception());
			logger.error("test error message");
			logger.info("test info message multiline\n... message line 2\n...message line 3");
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
