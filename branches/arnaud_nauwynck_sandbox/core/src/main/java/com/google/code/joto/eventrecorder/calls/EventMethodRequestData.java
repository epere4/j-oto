package com.google.code.joto.eventrecorder.calls;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 *
 */
public class EventMethodRequestData implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	private Object expr;
	private Method method;
	private Object[] arguments;
	
	// -------------------------------------------------------------------------
	
	public EventMethodRequestData(Object expr, Method method, Object[] arguments) {
		super();
		this.expr = expr;
		this.method = method;
		this.arguments = arguments;
	}

	// -------------------------------------------------------------------------

	public Object getExpr() {
		return expr;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArguments() {
		return arguments;
	}
	
}
