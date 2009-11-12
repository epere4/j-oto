package com.google.code.joto.eventrecorder;

import java.util.List;

/**
 *
 */
public interface RecordEventStore {

	public void open();
	public void close();
	public void purgeCache();
	
	/**
	 * get the event with "light" info ... no data is returned here!  
	 * @return
	 */
	public List<RecordEventHandle> getEvents();
	
	/**
	 * get the "heavy" info for a light event ... the real serializable data is returned here!
	 * @param handle
	 * @return
	 */
	public RecordEventData getEventData(RecordEventHandle handle);
		

	public int addEvent(RecordEventHandle info, byte[] data);
	
	public void addRecordEventListener(RecordEventListener p);
	public void removeRecordEventListener(RecordEventListener p);
	
}
