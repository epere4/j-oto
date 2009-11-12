package com.google.code.joto.eventrecorder.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.code.joto.eventrecorder.RecordEventChange;
import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventHandle;
import com.google.code.joto.eventrecorder.RecordEventChange.AddRecordEventStoreEvent;

/**
 *
 */
public class DefaultMemoryRecordEventStore extends AbstractRecordEventStore {
	
	protected List<RecordEventData> eventDataList = new ArrayList<RecordEventData>();
	
	// ------------------------------------------------------------------------

	public DefaultMemoryRecordEventStore() {
	}

	// ------------------------------------------------------------------------

	public synchronized List<RecordEventHandle> getEvents() {
		List<RecordEventHandle> res = eventDataListToEventHandleList(eventDataList);
		return res;
	}

	public synchronized RecordEventData getEventData(RecordEventHandle eventHandle) {
		if (eventDataList.isEmpty()) {
			return null;
		}
		int eventId = eventHandle.getEventId();
		int firstEventId = eventDataList.get(0).getEventId(); 
		int index = eventId - firstEventId;
		RecordEventData res;
		if (index < eventDataList.size()) {
			res = eventDataList.get(index);
		} else {
			// Should not occur!
			res = null;
		}
		return res;
	}

	
	public synchronized int addEvent(RecordEventHandle eventInfo, byte[] data) {
		RecordEventData eventData = createNewEventData(eventInfo, data);
		
		eventDataList.add(eventData);
		
		RecordEventChange storeEvt = new AddRecordEventStoreEvent(eventData);
		fireStoreEvent(storeEvt);
		
		return eventData.getEventId();
	}

}
