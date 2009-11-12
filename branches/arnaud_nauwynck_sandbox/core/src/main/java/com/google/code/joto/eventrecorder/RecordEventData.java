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

    /** */
    private static final long serialVersionUID = -1102569878095077812L;

    private final int eventId;
    
    private RecordEventHandle eventHandle;
    
    private byte[] objectData;
    
    /** TODO... for debug only?? */
    private transient Object cachedObjectCopy;
    
    // ------------------------------------------------------------------------

	public RecordEventData(int eventId, RecordEventHandle eventHandle, byte[] objectData) {
		this.eventId = eventId;
		this.eventHandle = eventHandle;
		this.objectData = objectData;
	}

	// ------------------------------------------------------------------------

	public int getEventId() {
		return eventId;
	}
	
	public RecordEventHandle getEventHandle() {
		return eventHandle;
	}

	public Object getObjectDataCopy() {
		if (cachedObjectCopy == null) {
			cachedObjectCopy = byteArrayToSerializable(objectData);
		}
		return cachedObjectCopy;
	}


	// ------------------------------------------------------------------------
	
	/*pp*/ void setEventHandle(RecordEventHandle p) {
		this.eventHandle = p;
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
