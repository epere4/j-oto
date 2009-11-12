package com.google.code.joto.eventrecorder;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public interface RecordEventStore {

	public void open();
	public void close();
	public void purgeCache();
	
	public List<RecordEventHandle> getEvents();
	public RecordEventData getEventData(RecordEventHandle handle);
		
	public void addEvent(RecordEventHandle info, Serializable serializableData);
	public void addEvent(RecordEventHandle info, byte[] data);
	
	public void addRecordEventListener(RecordEventListener p);
	public void removeRecordEventListener(RecordEventListener p);
	
}
