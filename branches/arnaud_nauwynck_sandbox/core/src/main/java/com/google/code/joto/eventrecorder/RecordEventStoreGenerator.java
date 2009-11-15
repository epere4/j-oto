package com.google.code.joto.eventrecorder;

import java.io.Serializable;

/**
 * helper class for eventStore
 * This call is used as a proxy to RecordEventStore, that can be enabled/disabled   
 */
public class RecordEventStoreGenerator {

	private RecordEventStore eventStore;

	public boolean enableGenerator = true;
	
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
	
	public int addEvent(RecordEventSummary info, Serializable objData) {
		if (!enableGenerator) {
			return -1;
		}
		byte[] objDataBytes = RecordEventData.serializableToByteArray(objData);
		int res = eventStore.addEvent(info, objDataBytes).getEventId();
		return res;
	}
	
	
}
