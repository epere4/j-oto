package com.google.code.joto.eventrecorder.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.code.joto.eventrecorder.RecordEventChange;
import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventHandle;
import com.google.code.joto.eventrecorder.RecordEventListener;
import com.google.code.joto.eventrecorder.RecordEventStore;

/**
 * 
 */
public abstract class AbstractRecordEventStore implements RecordEventStore  {

	protected int eventIdGenerator = 1;

	private List<RecordEventListener> listeners = new ArrayList<RecordEventListener>();
	
	// ------------------------------------------------------------------------

	public AbstractRecordEventStore() {
	}

	// ------------------------------------------------------------------------
	
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

	// ------------------------------------------------------------------------

	public void open() {
		// do nothing
	}
	
	public void close() {
		// do nothing
	}

	public void purgeCache() {
		// do nothing
	}
	
	public void addEvent(RecordEventHandle info, Serializable objectData) {
		byte[] data = RecordEventData.serializableToByteArray(objectData);
		
		addEvent(info, data);
	}
	
	protected static List<RecordEventHandle> eventDataListToEventHandleList(List<RecordEventData> eventDataList) {
		List<RecordEventHandle> res = new ArrayList<RecordEventHandle>();
		for(RecordEventData eventData : eventDataList) {
			res.add(eventData.getEventHandle());
		}
		return res;
	}
	
	protected RecordEventData createNewEventData(RecordEventHandle eventInfo, byte[] data) {
		int newEventId = eventIdGenerator++;
		RecordEventHandle eventHandle = new RecordEventHandle(newEventId, eventInfo);
		RecordEventData eventData = new RecordEventData(newEventId, eventHandle, data);
		return eventData;
	}
	
	protected void fireStoreEvent(RecordEventChange event) {
		List<RecordEventListener> listenersCopy = listeners;
		for(RecordEventListener elt : listenersCopy) {
			try {
				elt.onEvent(event);
			} catch(Exception ex) {
				System.err.println("Failed to fire event to listener ... ignore, no rethrow!");
			}
		}
	}
	
}
