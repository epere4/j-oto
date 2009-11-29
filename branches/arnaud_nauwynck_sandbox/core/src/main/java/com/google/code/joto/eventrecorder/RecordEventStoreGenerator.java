package com.google.code.joto.eventrecorder;

import java.io.Serializable;

/**
 * helper class for eventStore
 * This call is used as a proxy to RecordEventStore, that can be enabled/disabled   
 */
public class RecordEventStoreGenerator {

	protected RecordEventStore eventStore;

	protected boolean enableGenerator = true;
	
	// -------------------------------------------------------------------------
	
	public RecordEventStoreGenerator(RecordEventStore eventStore) {
		super();
		this.eventStore = eventStore;
	}

	// -------------------------------------------------------------------------
	
	public boolean isEnableGenerator() {
		return enableGenerator;
	}

	public void setEnableGenerator(boolean enableGenerator) {
		this.enableGenerator = enableGenerator;
	}
	
	// -------------------------------------------------------------------------
	
	public void addEvent(RecordEventSummary info, Serializable objData, 
				RecordEventStoreCallback callback) {
		if (!enableGenerator) {
			return ;
		}
		RecordEventData eventData = eventStore.addEvent(info, objData);
		if (callback != null) {
			callback.onStore(eventData);
		}
	}
	
	
}
