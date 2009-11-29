package com.google.code.joto.eventrecorder.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStoreChange;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.TruncateRecordEventStoreEvent;

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
	public synchronized RecordEventData addEvent(RecordEventSummary eventInfo, Serializable objData) {
		RecordEventData eventData = doAddEvent(eventInfo, objData);
		
		AddRecordEventStoreEvent addEvent = new AddRecordEventStoreEvent(eventData);
		if (maxEventCount != -1 || eventDataList.size() < maxEventCount) {
			// no need truncate
			fireStoreEvent(addEvent);
		} else {
			// need truncate
			RecordEventData truncatedEvent = super.eventDataList.remove(0);
			int truncatedEventId = truncatedEvent.getEventId();
			List<RecordEventSummary> truncatedEvents = new ArrayList<RecordEventSummary>(1);
			truncatedEvents.add(truncatedEvent.getEventSummary());
			TruncateRecordEventStoreEvent truncEvent = onTruncateSetFirstEventId(truncatedEventId + 1, truncatedEvents);
			
			List<RecordEventStoreChange> firedEvents = new ArrayList<RecordEventStoreChange>(2);
			firedEvents.add(truncEvent);
			firedEvents.add(addEvent);
			fireStoreEvents(firedEvents);
		}
		return eventData;
	}

	// internal
	// ------------------------------------------------------------------------
	
	protected void checkNeedTruncate() {
		if (maxEventCount == -1 || eventDataList.size() < maxEventCount) {
			return;
		}
		// get truncated info
		int truncatedLen = eventDataList.size() - maxEventCount;
		List<RecordEventSummary> truncateEvents = eventDataListToEventHandleList(eventDataList.subList(0, truncatedLen));
		int fromEventId = truncateEvents.get(0).getEventId();
		int toEventId = truncateEvents.get(truncatedLen - 1).getEventId() +  1;
		
		// do truncate
		int checkTruncatedLen = eventDataList.truncateHeadForMaxRows(maxEventCount);
		assert checkTruncatedLen == truncatedLen;

		// fire truncated event
		fireStoreEvent(new TruncateRecordEventStoreEvent(fromEventId, toEventId, truncateEvents));
	}

}