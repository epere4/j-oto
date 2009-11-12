package com.google.code.joto.eventrecorder.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventHandle;
import com.google.code.joto.eventrecorder.RecordEventChange.TruncateRecordEventStoreEvent;

/**
 *
 */
public class CyclicBufferRecordEventStore extends DefaultMemoryRecordEventStore {

	private int maxEventCount = 100; 
	
	// ------------------------------------------------------------------------

	public CyclicBufferRecordEventStore() {
	}

	
	// ------------------------------------------------------------------------

	public int getMaxEventCount() {
		return maxEventCount;
	}

	public void setMaxEventCount(int p) {
		this.maxEventCount = p;
		checkNeedTruncate();
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public synchronized void addEvent(RecordEventHandle eventInfo, byte[] data) {
		super.addEvent(eventInfo, data);
		
		// truncate if necessary..
		checkNeedTruncate();
	}
	
	// internal
	// ------------------------------------------------------------------------
	
	protected void checkNeedTruncate() {
		int eventDataListLen = eventDataList.size();
		int truncateCount = 0;
		if (eventDataListLen > maxEventCount) {
			// need truncate...
			truncateCount = maxEventCount - eventDataListLen;
		}
		
		if (truncateCount != 0) {
			List<RecordEventHandle> truncateEvents = eventDataListToEventHandleList(eventDataList.subList(0, truncateCount));
			
			List<RecordEventData> newList = new ArrayList<RecordEventData>(eventDataList.subList(truncateCount, eventDataListLen));
			eventDataList.clear();
			eventDataList.addAll(newList);
			
			fireStoreEvent(new TruncateRecordEventStoreEvent(truncateEvents));
		}
	}


}
