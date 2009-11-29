package com.google.code.joto.eventrecorder.ext.log;

import java.util.Date;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;

import com.google.code.joto.eventrecorder.RecordEventStoreGenerator;
import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 *
 */
public class EventStoreGeneratorLogbackAppender extends AppenderBase<ILoggingEvent> {
	
	private RecordEventStoreGenerator eventGenerator;
	
	private String eventType = "logback";
	
	// -------------------------------------------------------------------------
	
	public EventStoreGeneratorLogbackAppender(RecordEventStoreGenerator eventGenerator, String eventType) {
		super();
		this.eventGenerator = eventGenerator;
		this.eventType = eventType;
	}

	// -------------------------------------------------------------------------

	@Override
	public void start() {
		// cf logback 0.9.17 ?
//		if (this.layout == null) {
//			addError("No layout set for the appender named [" + name + "].");
//			return;
//		}

		super.start();
	}

	@Override
	protected void append(ILoggingEvent p) {
		if (eventGenerator == null || !eventGenerator.isEnableGenerator()) {
			return;
		}
		
		RecordEventSummary eventInfo = new RecordEventSummary(-1);
		LogbackEventData eventData = new LogbackEventData();

		eventInfo.setEventType(eventType);
		eventData.setLevel(eventInfo, p.getLevel().toString());

		eventInfo.setThreadName(p.getThreadName());
		eventInfo.setEventDate(new Date(p.getTimeStamp()));
		eventInfo.setEventMethodName(p.getLoggerName());

		// use message instead of formattedMessage for header!!
		// => use "... {0} .. {1} .." to compress event summary encoding
		eventInfo.setEventMethodDetail(p.getMessage()); 
		

		eventData.setFormattedMessage(p.getFormattedMessage());
		eventData.setArgumentArray(p.getArgumentArray());

		if (p.getThrowableProxy() != null) {
			IThrowableProxy throwableProxy = p.getThrowableProxy();
			// throwableProxy.getClassName()
			// throwableProxy.getMessage()
			StackTraceElementProxy[] traceElts = throwableProxy.getStackTraceElementProxyArray();
			eventData.setStackTraceElements(traceElts);
		}
		
		Map<String,String> mdcPropertyMap = p.getMDCPropertyMap();
		if (mdcPropertyMap != null && !mdcPropertyMap.isEmpty()) {
			eventData.setMDCPropertyMap(mdcPropertyMap);
		}
		
		
		eventGenerator.addEvent(eventInfo , eventData, null);
	}

}

