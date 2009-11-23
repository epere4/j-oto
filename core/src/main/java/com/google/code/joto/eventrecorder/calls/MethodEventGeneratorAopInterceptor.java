package com.google.code.joto.eventrecorder.calls;

import java.lang.reflect.Method;
import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.code.joto.eventrecorder.RecordEventStoreGenerator;
import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * AOP-Alliance support class for recording events to EventStore 
 */
public class MethodEventGeneratorAopInterceptor implements MethodInterceptor {

	private RecordEventStoreGenerator eventGenerator;
	
	private String eventType;
	private String requestEventSubType;
	private String responseEventSubType;
	
	private ObjectReplacementMap objectReplacementMap;
	
	//-------------------------------------------------------------------------

	public MethodEventGeneratorAopInterceptor(
			RecordEventStoreGenerator eventGenerator,
			String eventType,
			String requestEventSubType,
			String responseEventSubType
			) {
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
	public Object invoke(MethodInvocation methInvocation) throws Throwable {
		Method method = methInvocation.getMethod();
		String methodName = method.getName();
		Object target = methInvocation.getThis();
		Object[] args = methInvocation.getArguments(); 
			
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

