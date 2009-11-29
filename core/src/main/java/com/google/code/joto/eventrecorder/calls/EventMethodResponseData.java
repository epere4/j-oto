package com.google.code.joto.eventrecorder.calls;

import java.io.Serializable;

/**
 *
 */
public class EventMethodResponseData implements Serializable {

	/** intenral for javA.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	private Object result;
	private Throwable exception;
	
	// -------------------------------------------------------------------------
	
	public EventMethodResponseData() {
	}
	
	public EventMethodResponseData(Object result, Throwable exception) {
		this.result = result;
		this.exception = exception;
	}

	// -------------------------------------------------------------------------

	public Object getResult() {
		return result;
	}

	public void setResult(Object p) {
		this.result = p;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable p) {
		this.exception = p;
	}
	
}
