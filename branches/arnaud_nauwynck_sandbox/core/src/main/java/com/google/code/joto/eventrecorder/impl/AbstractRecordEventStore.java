package com.google.code.joto.eventrecorder.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventListener;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventStoreChange;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.TruncateRecordEventStoreEvent;

/**
 * abstract helper base-class for RecordEventStore implementations
 */
public abstract class AbstractRecordEventStore implements RecordEventStore {

	private Logger log = Logger.getLogger(getClass());
	
	protected boolean readonly;
	
	
	private List<RecordEventListener> listeners = new ArrayList<RecordEventListener>();
	
	/**
	 * first id of event available, i.e. not truncated
	 */
	private int firstEventId = 1;
	
	/**
	 * exclusive last event id... also serves as idGenerator!
	 */
	private int lastEventId = 1; // exclusive
	
	// ------------------------------------------------------------------------

	public AbstractRecordEventStore() {
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void open(boolean appendOtherwiseReadonly) {
		this.readonly = !appendOtherwiseReadonly;
		// do nothing
	}
	
	@Override
	public void close() {
		// do nothing
	}

	@Override
	public void flush() {
		// do nothing
	}
	
	@Override
	public final int getFirstEventId() {
		return firstEventId;
	}
	
	@Override
	public final int getLastEventId() {
		return lastEventId;
	}
	
	@Override
	public final int getEventsCount() {
		return lastEventId - firstEventId;
	}

	@Override
	public final int getFirstEventIdWithMaxCount(int maxCount) {
		int count = lastEventId - firstEventId;
		if (maxCount != -1 && count > maxCount) {
			count = maxCount;
		}
		return lastEventId - count;
	}

	
	protected TruncateRecordEventStoreEvent onTruncateSetFirstEventId(int p, List<RecordEventSummary> optTruncatedEvents) {
		int truncateFromEventId = firstEventId;  
		this.firstEventId = p;
		// TODO sanity check if optTruncatedEvents is given...
		return new TruncateRecordEventStoreEvent(truncateFromEventId, firstEventId, optTruncatedEvents); 
	}
	
	
	public synchronized void addRecordEventListener(RecordEventListener p) {
		List<RecordEventListener> tmp = new ArrayList<RecordEventListener>(listeners);
		tmp.add(p);
		this.listeners = tmp;
	}

	public synchronized void removeRecordEventListener(RecordEventListener p) {
		List<RecordEventListener> tmp = new ArrayList<RecordEventListener>(listeners);
		tmp.remove(p);
		this.listeners = tmp;
	}

	
	@Override
	public synchronized void getEventsAndAddEventListener(
			int fromEventId,
			RecordEventListener listener) {
		// 1) fire replayed events
		List<RecordEventStoreChange> replayStoreEvts = new ArrayList<RecordEventStoreChange>();
		List<RecordEventSummary> events = getEvents(fromEventId, -1);
		for(RecordEventSummary event : events) {
			RecordEventStoreChange storeEvt = new AddRecordEventStoreEvent(event);
			replayStoreEvts.add(storeEvt);
		}
		listener.onEvents(replayStoreEvts);
		
		// 2) add listener
		addRecordEventListener(listener);
	}
	
	public RecordEventData addEvent(RecordEventSummary info, Serializable objectData) {
		byte[] data = RecordEventData.serializableToByteArray(objectData);
		return addEvent(info, data);
	}
	
	public synchronized RecordEventData addEvent(RecordEventSummary event, byte[] data) {
		RecordEventData eventData = doAddEvent(event, data);
		fireStoreEvent(new AddRecordEventStoreEvent(eventData));
		return eventData;
	}

	protected abstract RecordEventData doAddEvent(RecordEventSummary info, byte[] data);

	protected static List<RecordEventSummary> eventDataListToEventHandleList(List<RecordEventData> eventDataList) {
		List<RecordEventSummary> res = new ArrayList<RecordEventSummary>();
		for(RecordEventData eventData : eventDataList) {
			res.add(eventData.getEventSummary());
		}
		return res;
	}
	
	protected RecordEventData createNewEventData(RecordEventSummary eventInfo, byte[] data) {
		int newEventId = lastEventId++;
		RecordEventSummary event = new RecordEventSummary(newEventId, eventInfo);
		RecordEventData eventData = new RecordEventData(event, data);
		return eventData;
	}
	
	protected void fireStoreEvent(RecordEventStoreChange event) {
		List<RecordEventListener> listenersCopy = listeners;
		for(RecordEventListener elt : listenersCopy) {
			try {
				elt.onEvent(event);
			} catch(Exception ex) {
				log.warn("Failed to fire event to listener ... ignore, no rethrow!", ex);
			}
		}
	}

	protected void fireStoreEvents(List<RecordEventStoreChange> events) {
		List<RecordEventListener> listenersCopy = listeners;
		for(RecordEventListener elt : listenersCopy) {
			try {
				elt.onEvents(events);
			} catch(Exception ex) {
				log.warn("Failed to fire event to listener ... ignore, no rethrow!", ex);
			}
		}
	}
	
}
