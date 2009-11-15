package com.google.code.joto.eventrecorder.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.io.IdToStringMapCompressionContext;

/**
 * internal helper class for encoding/decoding EventRecordSummary 
 * in a contextual compressed way
 */
public class EventSummaryCompressionContext {

	private IdToStringMapCompressionContext threadNameCtx = new IdToStringMapCompressionContext();
	private IdToStringMapCompressionContext eventTypeCtx = new IdToStringMapCompressionContext(); 
	private IdToStringMapCompressionContext eventSubTypeCtx = new IdToStringMapCompressionContext(); 
	private IdToStringMapCompressionContext eventMethodNameCtx = new IdToStringMapCompressionContext();
	// not compressed?? private IdToStringMapCompressionContext eventMethodDetail;

	// -------------------------------------------------------------------------
	
	public EventSummaryCompressionContext() {
	}

	//-------------------------------------------------------------------------

	
	public void encode(RecordEventSummary src, DataOutputStream out) throws IOException {
		// not encoded here... eventId;
		out.writeLong(src.getEventDate().getTime());
		threadNameCtx.encode(src.getThreadName(), out);
		eventTypeCtx.encode(src.getEventType(), out);
		eventSubTypeCtx.encode(src.getEventSubType(), out);
		eventMethodNameCtx.encode(src.getEventMethodName(), out);
		String eventMethodDetail = src.getEventMethodDetail();
		out.writeBoolean(eventMethodDetail != null);
		if (eventMethodDetail != null) {
			out.writeUTF(eventMethodDetail);
		}
		// not encoded here... internalEventStoreDataAddress;
	}
	
	public RecordEventSummary decode(int eventId, DataInputStream in) throws IOException {
		RecordEventSummary res = new RecordEventSummary(eventId);
		
		// not decoded here... eventId
		res.setEventDate(new Date(in.readLong()));
		res.setThreadName(threadNameCtx.decode(in));
		res.setEventType(eventTypeCtx.decode(in));
		res.setEventSubType(eventSubTypeCtx.decode(in));
		res.setEventMethodName(eventMethodNameCtx.decode(in));
		boolean isEventMethodDetail = in.readBoolean();
		if (isEventMethodDetail) {
			res.setEventMethodDetail(in.readUTF());
		}
		// not decoded here... internalEventStoreDataAddress;

		return res;
	}
	
}
