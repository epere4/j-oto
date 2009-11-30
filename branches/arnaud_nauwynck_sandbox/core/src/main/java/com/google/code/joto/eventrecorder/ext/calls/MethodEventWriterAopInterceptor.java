package com.google.code.joto.eventrecorder.ext.calls;

import java.lang.reflect.Method;
import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;
import com.google.code.joto.eventrecorder.writer.RecordEventWriterCallback.CorrelatedEventSetterCallback;

/**
 * AOP-Alliance support class for recording events to EventStore 
 */
public class MethodEventWriterAopInterceptor implements MethodInterceptor {

	private RecordEventWriter eventWriter;
	
	private String eventType;
	private String requestEventSubType;
	private String responseEventSubType;
	
	private ObjectReplacementMap objectReplacementMap;
	
	//-------------------------------------------------------------------------

	public MethodEventWriterAopInterceptor(
			RecordEventWriter eventWriter,
			String eventType,
			String requestEventSubType,
			String responseEventSubType
			) {
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
	public Object invoke(MethodInvocation methInvocation) throws Throwable {
		Method method = methInvocation.getMethod();
		String methodName = method.getName();
		Object target = methInvocation.getThis();
		Object[] args = methInvocation.getArguments(); 

		boolean enable = eventWriter.isEnable();
		if (!enable) {
			// *** do call ***
			Object res = method.invoke(target, args);
			return res;
		} else {
			// generate event for method request

			RecordEventSummary evt = createEvent(methodName, requestEventSubType);
			Object replTarget = target;
			Object[] replArgs = args; // TODO not required to replace arg sin current version?
			if (objectReplacementMap != null) {
				replTarget = objectReplacementMap.checkReplace(replTarget);
				replArgs = objectReplacementMap.checkReplaceArray(replArgs);
			}
			EventMethodRequestData reqObjData = new EventMethodRequestData(replTarget, method, replArgs);
			
			RecordEventSummary responseEvt = createEvent(methodName, responseEventSubType);
			CorrelatedEventSetterCallback callbackForEventId =
				new CorrelatedEventSetterCallback(responseEvt);
			
			eventWriter.addEvent(evt, reqObjData, callbackForEventId);

			try {
				// *** do call ***
				Object res = method.invoke(target, args);
	
				Object replRes = res;
				if (objectReplacementMap != null) {
					replRes = objectReplacementMap.checkReplace(res);
				}
				EventMethodResponseData respObjData = new EventMethodResponseData(replRes, null);
				eventWriter.addEvent(responseEvt, respObjData, null);

				return res;
	
			} catch(Exception ex) {
				EventMethodResponseData respObjData = new EventMethodResponseData(null, ex);
				eventWriter.addEvent(responseEvt, respObjData, null);
	
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

