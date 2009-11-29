package com.google.code.joto.eventrecorder.ext.log;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.google.code.joto.eventrecorder.RecordEventStoreGenerator;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.io.SerializableUtil;

/**
 * Spi Extension of EventRecorderStore, for storing log4j events as events 
 *
 */
public class EventStoreGeneratorLog4jAppender extends AppenderSkeleton {

	private RecordEventStoreGenerator eventGenerator;
	
	private String eventType = "log4j";
	// private String eventSubType => used for logEvent message severity 

	
	// -------------------------------------------------------------------------
	
	public EventStoreGeneratorLog4jAppender(RecordEventStoreGenerator eventGenerator, String eventType) {
		super();
		this.eventGenerator = eventGenerator;
		this.eventType = eventType;
	}

	// -------------------------------------------------------------------------

	@Override
	public void close() {
		eventGenerator = null;
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent p) {
		if (eventGenerator != null && eventGenerator.isEnableGenerator()) {
			RecordEventSummary eventInfo = new RecordEventSummary(-1);

			eventInfo.setEventType(eventType);
			eventInfo.setEventSubType(p.getLevel().toString());

			eventInfo.setThreadName(p.getThreadName());
			eventInfo.setEventDate(new Date(p.getTimeStamp()));
			eventInfo.setEventMethodName(p.getLoggerName());
			
			eventInfo.setEventMethodDetail(p.getRenderedMessage());
			
			// filla additionnal LoggingEvent values in eventData
			Log4jEventData eventData = new Log4jEventData();

			if (p.getThrowableInformation() != null) {
				Throwable throwable = p.getThrowableInformation().getThrowable();
				if (SerializableUtil.checkSerializable(throwable)) {
					eventData.setThrowable(throwable);
				} else {
					// set only when throwable is not serializable? ... otherwise redundant with Throwable...
					String[] throwableStrRep = p.getThrowableStrRep();
					eventData.setThrowableStrRep(throwableStrRep);
				}
			}
			
			Map<String,String> properties = p.getProperties();
			if (properties != null && !properties.isEmpty()) {
				eventData.setProperties(properties);
			}
			
			String ndc = p.getNDC();
			if (ndc != null) {
				// TODO?? NDC. should be / be-converted to String[] ??
				eventData.setNdcStrRep(ndc);
			}
			
			eventGenerator.addEvent(eventInfo , eventData, null);
		}
		
	}


	// -------------------------------------------------------------------------
	
	
}
