package com.google.code.joto.eventrecorder.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private Logger log = LoggerFactory.getLogger(getClass());
	
	protected boolean canRead;
	protected boolean canWriteAppend;
	
	
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
	public void open(String mode) {
		setMode(mode);
	}
	
	protected void setMode(String mode) {
		if (mode.equals("ra")) {
			canRead = true;
			canWriteAppend = true;
		} else if (mode.equals("rw")) {
			canRead = true;
			canWriteAppend = true;
		} else if (mode.equals("r")) {
			canRead = true;
			canWriteAppend = false;
		} else {
			throw new IllegalArgumentException("invalid mode '" + mode + "', expecting one of { ra, rw, r }");
		}
	}
	
	public boolean getCanRead() {
		return canRead;
	}

	public boolean getCanWriteAppend() {
		return canWriteAppend;
	}

	@Override
	public void close() {
		canRead = false;
		canWriteAppend = false;
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

	protected void initSetFirstEventId(int p) {
		this.firstEventId = p;
	}
	
	protected void setLastEventId(int p) {
		this.lastEventId = p;
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
	
	public synchronized RecordEventData addEvent(RecordEventSummary event, Serializable objData) {
		RecordEventData eventData = doAddEvent(event, objData);
		fireStoreEvent(new AddRecordEventStoreEvent(eventData));
		return eventData;
	}

	abstract RecordEventData doAddEvent(RecordEventSummary eventInfo, Serializable objData);

	protected static List<RecordEventSummary> eventDataListToEventHandleList(List<RecordEventData> eventDataList) {
		List<RecordEventSummary> res = new ArrayList<RecordEventSummary>();
		for(RecordEventData eventData : eventDataList) {
			res.add(eventData.getEventSummary());
		}
		return res;
	}
	
	protected RecordEventData createNewEventData(RecordEventSummary eventInfo, Object objData) {
		int newEventId = lastEventId++;
		RecordEventSummary event = new RecordEventSummary(newEventId, eventInfo);
		RecordEventData eventData = new RecordEventData(event, objData);
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
