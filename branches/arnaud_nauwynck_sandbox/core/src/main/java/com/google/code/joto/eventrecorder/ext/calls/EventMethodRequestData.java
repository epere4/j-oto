package com.google.code.joto.eventrecorder.ext.calls;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.google.code.joto.reflect.SerializableMethodRef;

/**
 *
 */
public class EventMethodRequestData implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	private Object expr;
	
	// java.lang.Method is not serializable!! => use Class+methodName+signature
	private SerializableMethodRef method;

	private Object[] arguments;
	
	// -------------------------------------------------------------------------
	
	public EventMethodRequestData(Object expr, Method method, Object[] arguments) {
		super();
		this.expr = expr;
		this.method = new SerializableMethodRef(method);
		this.arguments = arguments;
	}

	// -------------------------------------------------------------------------

	public Object getExpr() {
		return expr;
	}

	public Method getMethod() {
		return method.getMethod();
	}

	public Object[] getArguments() {
		return arguments;
	}
	
}
