package com.google.code.joto.eventrecorder.calls;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;

import com.google.code.joto.eventrecorder.RecordEventHandle;
import com.google.code.joto.eventrecorder.RecordEventStoreGenerator;

/**
 * default implementation of java Proxy reflection, 
 * for generating record events as request/response
 */
public class MethodEventGeneratorInvocationHandler implements InvocationHandler {

	private Object target;
	
	private RecordEventStoreGenerator eventGenerator;
	
	private String eventType;
	private String requestEventSubType;
	private String responseEventSubType;
	
	//-------------------------------------------------------------------------

	public MethodEventGeneratorInvocationHandler(Object target,
			RecordEventStoreGenerator eventGenerator,
			String eventType,
			String requestEventSubType,
			String responseEventSubType
			) {
		this.target = target;
		this.eventGenerator = eventGenerator;
		this.eventType = eventType;
		this.requestEventSubType = requestEventSubType;
		this.responseEventSubType = responseEventSubType;
	}

	//-------------------------------------------------------------------------

	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final String methodName = method.getName();

		// Handle the methods from java.lang.Object
		if (method.getDeclaringClass() == Object.class) {
			if (args == null && methodName.equals("toString")) {
				return "EventDataSourceProxy[" + target + "]";
			} else if (args == null && methodName.equals("hashCode")) {
				return target.hashCode() + 123;
			} else if (args.length == 1
					&& method.getParameterTypes()[0] == Object.class
					&& methodName.equals("equals")) {
				return proxy ==  args[0];
			} 
		} 

		// generate event for method request
		boolean enable = eventGenerator.isEnableGenerator();
		int requestEventId = -1;
		if (enable) {
			RecordEventHandle evt = createEvent(methodName, requestEventSubType);
			EventMethodRequestData objData = new EventMethodRequestData(target, method, args);
			requestEventId = eventGenerator.addEvent(evt, objData);
		}

		try {
			// *** do call ***
			Object res = method.invoke(this, args);

			if (enable && requestEventId != -1) {
				RecordEventHandle evt = createEvent(methodName, responseEventSubType);
				EventMethodResponseData objData = new EventMethodResponseData(requestEventId, res, null);
				eventGenerator.addEvent(evt, objData);
			}
			return res;

		} catch(Exception ex) {
			if (enable && requestEventId != -1) {
				RecordEventHandle evt = createEvent(methodName, responseEventSubType);
				EventMethodResponseData objData = new EventMethodResponseData(requestEventId, null, ex);
				eventGenerator.addEvent(evt, objData);
			}

			throw ex; // rethow!
		}

	}

	
	private RecordEventHandle createEvent(String methodName, String eventSubType) {
		RecordEventHandle evt = new RecordEventHandle(-1);
		evt.setEventDate(new Date());
		evt.setEventType(eventType);
		evt.setEventSubType(eventSubType);
		evt.setEventMethodName(methodName);
		return evt;
	}

}
