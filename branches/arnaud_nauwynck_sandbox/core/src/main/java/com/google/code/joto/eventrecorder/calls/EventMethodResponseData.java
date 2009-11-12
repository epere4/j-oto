package com.google.code.joto.eventrecorder.calls;

import java.io.Serializable;

/**
 *
 */
public class EventMethodResponseData implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	// private transient EventMethodRequestData correspondingRequestData; ??
	private int requestEventId;
	
	private Object result;
	private Throwable exception;
	
	// -------------------------------------------------------------------------
	
	public EventMethodResponseData(int requestEventId, Object result, Throwable exception) {
		super();
		this.requestEventId = requestEventId;
		this.result = result;
		this.exception = exception;
	}

	// -------------------------------------------------------------------------

	public int getRequestEventId() {
		return requestEventId;
	}
	
	public Object getResult() {
		return result;
	}

	public Throwable getException() {
		return exception;
	}
	
}
