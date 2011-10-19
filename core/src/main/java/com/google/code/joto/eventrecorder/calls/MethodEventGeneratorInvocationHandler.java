package com.google.code.joto.eventrecorder.calls;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;
import com.google.code.joto.eventrecorder.writer.RecordEventWriterCallback.CorrelatedEventSetterCallback;

/**
 * default implementation of java Proxy reflection, 
 * for generating record events as request/response
 */
public class MethodEventGeneratorInvocationHandler implements InvocationHandler {

	private Object target;
	
	private RecordEventWriter eventWriter;
	
	private String eventType;
	private String requestEventSubType;
	private String responseEventSubType;
	
	private ObjectReplacementMap objectReplacementMap;
	
	//-------------------------------------------------------------------------

	public MethodEventGeneratorInvocationHandler(Object target,
			RecordEventWriter eventWriter,
			String eventType,
			String requestEventSubType,
			String responseEventSubType
			) {
		this.target = target;
		this.eventWriter = eventWriter;
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
		boolean enable = eventWriter.isEnable();
		if (!enable) {
			// *** do call ***
			Object res = method.invoke(target, args);
			return res;
		} else {
 		
			RecordEventSummary evt = createEvent(methodName, requestEventSubType);
			Object replTarget = target;
			Object[] replArgs = args; // TODO not required to replace arg sin current version?
			if (objectReplacementMap != null) {
				replTarget = objectReplacementMap.checkReplace(replTarget);
				replArgs = objectReplacementMap.checkReplaceArray(replArgs);
			}
			EventMethodRequestData reqObjData = new EventMethodRequestData(replTarget, method, replArgs);
			
			RecordEventSummary respEvt = createEvent(methodName, responseEventSubType);
			CorrelatedEventSetterCallback callbackForEventId =
				new CorrelatedEventSetterCallback(respEvt);
			
			eventWriter.addEvent(evt, reqObjData, callbackForEventId);


			try {
				// *** do call ***
				Object res = method.invoke(target, args);
				
				Object replRes = res;
				if (objectReplacementMap != null) {
					replRes = objectReplacementMap.checkReplace(res);
				}
				EventMethodResponseData respObjData = new EventMethodResponseData(replRes, null);
				eventWriter.addEvent(respEvt, respObjData, null);

				return res;

			} catch(Exception ex) {
				EventMethodResponseData respObjData = new EventMethodResponseData(null, ex);
				eventWriter.addEvent(respEvt, respObjData, null);
	
				throw ex; // rethow!
			}
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
