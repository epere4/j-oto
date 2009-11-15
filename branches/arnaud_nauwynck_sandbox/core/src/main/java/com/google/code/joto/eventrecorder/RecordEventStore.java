package com.google.code.joto.eventrecorder;

import java.util.List;

/**
 *
 */
public interface RecordEventStore {

	public void open(boolean appendOtherwiseReadonly);
	public void close();
	public void flush();

	public int getEventsCount();
	public int getFirstEventId();
	public int getLastEventId();
	
	public int getFirstEventIdWithMaxCount(int maxCount);
	
	/**
	 * get the event with summary "light" info ... no data is returned here!
	 * @param fromEventId
	 * @param toEventId (exclusive), can use -1 for last event  
	 * @return
	 */
	public List<RecordEventSummary> getEvents(int fromEventId, int toEventId);
	
	/**
	 * get the "heavy" info for a light event ... the real serializable data is returned here!
	 * @param handle
	 * @return
	 */
	public RecordEventData getEventData(RecordEventSummary handle);

	
	/**
	 * purge events history from beginning to <code>toEventId</code>
	 * @param toEventId, can use -1 for purge until last event
	 */
	public void purgeEvents(int toEventId);

	/**
	 * SPI to add a new stored event
	 * You should typically call this only from helper class RecordEventStoreGenerator,
	 * which can offer simpler api methods, and can be enabled/disabled at runtime. 
	 */
	public RecordEventData addEvent(RecordEventSummary info, byte[] data);

	/**
	 * synchronized method to call <code>addRecordEventListener(listener) + getEvents(fromEventId, -1)</code> 
	 * This is usefull to retrieve events from past, and synchronize with present 
	 * ... without having to removed duplicates
	 * @param fromEventId
	 * @param listener 
	 */
	public void getEventsAndAddEventListener(int fromEventId, RecordEventListener listener);

	public void addRecordEventListener(RecordEventListener listener);
	public void removeRecordEventListener(RecordEventListener listener);
	
}
