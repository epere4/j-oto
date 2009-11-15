package com.google.code.joto.eventrecorder;

import java.util.List;

/**
 * default implementation of RecordEventListener for redispatching to a RecordEventVisitor
 */
public class DefaultVisitorRecordEventListener implements RecordEventListener {

	private RecordEventChangeVisitor target;
	
	//-------------------------------------------------------------------------

	public DefaultVisitorRecordEventListener(RecordEventChangeVisitor target) {
		this.target = target;
	}

	//-------------------------------------------------------------------------

	
	public void onEvent(RecordEventStoreChange event) {
		onEventStoreEvent(event);
	}
	
	public void onEvents(List<RecordEventStoreChange> events) {
		for(RecordEventStoreChange event : events) {
			onEventStoreEvent(event);
		}
	}
	
	protected void onEventStoreEvent(RecordEventStoreChange event) {
		try {
			event.accept(target);			
		} catch(Exception ex) {
			// ignore, no rethrow!
			System.err.println("Failed to handle RecordEvent...ignore!");
		}
	}
}
