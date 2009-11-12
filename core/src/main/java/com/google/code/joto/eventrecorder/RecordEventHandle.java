package com.google.code.joto.eventrecorder;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 */
public class RecordEventHandle implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	private final int eventId;
	private Date eventDate;
	private String eventType;
	private String eventSubType;
	private String eventMethodName;
	private String eventMethodDetail;
	
	private long objectDataFilePosition;
	
	// ------------------------------------------------------------------------

	public RecordEventHandle(int eventId) {
		this.eventId = eventId;
	}

	public RecordEventHandle(int eventId, Date eventDate, String eventType, String eventSubType, String eventMethodName,
			String eventMethodDetail, int objectDataFilePosition) {
		super();
		this.eventId = eventId;
		this.eventDate = eventDate;
		this.eventType = eventType;
		this.eventSubType = eventSubType;
		this.eventMethodName = eventMethodName;
		this.eventMethodDetail = eventMethodDetail;
		this.objectDataFilePosition = objectDataFilePosition;
	}

	public RecordEventHandle(int eventId, RecordEventHandle src) {
		this.eventId = eventId;
		this.eventDate = src.eventDate;
		this.eventType = src.eventType;
		this.eventSubType = src.eventSubType;
		this.eventMethodName = src.eventMethodName;
		this.eventMethodDetail = src.eventMethodDetail;
		this.objectDataFilePosition = src.objectDataFilePosition;
	}
	
	// ------------------------------------------------------------------------

	public int getEventId() {
		return eventId;
	}
	
	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String p) {
		this.eventType = p;
	}

	public String getEventSubType() {
		return eventSubType;
	}

	public void setEventSubType(String p) {
		this.eventSubType = p;
	}

	public String getEventMethodName() {
		return eventMethodName;
	}

	public void setEventMethodName(String eventMethodName) {
		this.eventMethodName = eventMethodName;
	}

	public String getEventMethodDetail() {
		return eventMethodDetail;
	}

	public void setEventMethodDetail(String eventMethodDetail) {
		this.eventMethodDetail = eventMethodDetail;
	}

	public long getObjectDataFilePosition() {
		return objectDataFilePosition;
	}

	public void setObjectDataFilePosition(long p) {
		this.objectDataFilePosition = p;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "RecordEventHandle[" 
			+ eventId 
			+ " " + eventDate
			+ " " + eventType 
			+ ((eventSubType != null)? " " + eventSubType : "") 
			+ " " + eventMethodName 
			+ ((eventMethodDetail != null)? " " + eventMethodDetail : "")
			+ " @" + objectDataFilePosition
			+ "]";
	}
	
}
