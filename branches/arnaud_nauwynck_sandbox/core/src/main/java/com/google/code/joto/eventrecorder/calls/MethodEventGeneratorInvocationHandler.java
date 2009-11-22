package com.google.code.joto.eventrecorder.calls;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;

import com.google.code.joto.eventrecorder.RecordEventSummary;
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
	
	private ObjectReplacementMap objectReplacementMap;
	
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

	
	public ObjectReplacementMap getObjectReplacementMap() {
		return objectReplacementMap;
	}

	public void setObjectReplacementMap(ObjectReplacementMap p) {
		this.objectReplacementMap = p;
	}


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
			RecordEventSummary evt = createEvent(methodName, requestEventSubType);
			Object replTarget = target;
			Object[] replArgs = args; // TODO not required to replace arg sin current version?
			if (objectReplacementMap != null) {
				replTarget = objectReplacementMap.checkReplace(replTarget);
				replArgs = objectReplacementMap.checkReplaceArray(replArgs);
			}
			EventMethodRequestData objData = new EventMethodRequestData(replTarget, method, replArgs);
			requestEventId = eventGenerator.addEvent(evt, objData);
		}

		try {
			// *** do call ***
			Object res = method.invoke(target, args);

			if (enable && requestEventId != -1) {
				RecordEventSummary evt = createEvent(methodName, responseEventSubType);
				
				Object replRes = res;
				if (objectReplacementMap != null) {
					replRes = objectReplacementMap.checkReplace(res);
				}
				EventMethodResponseData objData = new EventMethodResponseData(requestEventId, replRes, null);
				eventGenerator.addEvent(evt, objData);
			}
			return res;

		} catch(Exception ex) {
			if (enable && requestEventId != -1) {
				RecordEventSummary evt = createEvent(methodName, responseEventSubType);
				EventMethodResponseData objData = new EventMethodResponseData(requestEventId, null, ex);
				eventGenerator.addEvent(evt, objData);
			}

			throw ex; // rethow!
		}

	}

	
	private RecordEventSummary createEvent(String methodName, String eventSubType) {
		RecordEventSummary evt = new RecordEventSummary(-1);
		evt.setEventDate(new Date());
		evt.setEventType(eventType);
		evt.setEventSubType(eventSubType);
		evt.setEventMethodName(methodName);
		return evt;
	}

}
