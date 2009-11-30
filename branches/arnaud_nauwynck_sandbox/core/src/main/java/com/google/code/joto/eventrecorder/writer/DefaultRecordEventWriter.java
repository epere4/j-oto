package com.google.code.joto.eventrecorder.writer;

import java.io.Serializable;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * helper class for eventStore
 * This call is used as a proxy to RecordEventStore, that can be enabled/disabled   
 */
public class DefaultRecordEventWriter extends AbstractRecordEventWriter {

	// -------------------------------------------------------------------------
	
	public DefaultRecordEventWriter(RecordEventStore eventStore) {
		super(eventStore);
	}

	// -------------------------------------------------------------------------
	
	public void addEvent(RecordEventSummary info, Serializable objData, 
				RecordEventWriterCallback callback) {
		if (!enable) {
			return ;
		}
		RecordEventData eventData = eventStore.addEvent(info, objData);
		if (callback != null) {
			callback.onStore(eventData);
		}
	}
		
}
