package com.google.code.joto.eventrecorder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 */
public final class RecordEventData implements Serializable {

    /** internal for java.io.Serializable */
    private static final long serialVersionUID = 1L;

    private RecordEventSummary eventSummary;
    
    private byte[] objectDataBytes;
    
    /** TODO... for debug only?? */
    private transient Object cachedObjectCopy;
    
    // ------------------------------------------------------------------------

	public RecordEventData(RecordEventSummary eventSummary, byte[] objectData) {
		this.eventSummary = eventSummary;
		this.objectDataBytes = objectData;
	}

	// ------------------------------------------------------------------------

	public int getEventId() {
		return eventSummary.getEventId();
	}
	
	public RecordEventSummary getEventSummary() {
		return eventSummary;
	}

	public byte[] getObjectDataBytes() {
		return objectDataBytes; // TODO return safe copy?
	}

	public Object getObjectDataCopy() {
		if (cachedObjectCopy == null) {
			cachedObjectCopy = byteArrayToSerializable(objectDataBytes);
		}
		return cachedObjectCopy;
	}

	// ------------------------------------------------------------------------
	
	/*pp*/ void setEventSummary(RecordEventSummary p) {
		this.eventSummary = p;
	}

	
	

	/** ObjectInputStream utility method */
	public static Object byteArrayToSerializable(byte[] data) {
		Object res;
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(data); 
			ObjectInputStream oin = new ObjectInputStream(bin);
			res = oin.readObject();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	/** ObjectOutputStream utility method */
	public static byte[] serializableToByteArray(Serializable objectData) {
		byte[] res;
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
			ObjectOutputStream oout = new ObjectOutputStream(bout);
			oout.writeObject(objectData);
			oout.flush();
			res = bout.toByteArray();
		} catch(Exception ex) {
			throw new RuntimeException("Failed to serialize obj data", ex);
		}
		return res;
	}
}
