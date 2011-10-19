package com.google.code.joto.eventrecorder.ext.calls;

import java.io.PrintStream;
import java.lang.reflect.Method;

import com.google.code.joto.ObjectToCodeGenerator;
import com.google.code.joto.ast.beanstmt.impl.BeanASTPrettyPrinter;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.ext.calls.ObjectReplacementMap.ObjectInstanceReplacement;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;
import com.google.code.joto.reflect.ReflectUtils;

/**
 * Formatter for processing RecordEvent(s) corresponding to Method calls Request/Reply
 * and converting to String as Java "call" code,
 *  
 * using ObjectToCodeGenerator for expression/parameters + check object replacements 
 */
public class MethodCallToCodeRecordEventsProcessor implements RecordEventsProcessor {

	public static class Factory implements RecordEventsProcessorFactory<PrintStream> {
		private ObjectToCodeGenerator objToCode;
		private ObjectReplacementMap objectReplacementMap;
		
		public Factory(ObjectToCodeGenerator objToCode,
				ObjectReplacementMap objectReplacementMap) {
			super();
			this.objToCode = objToCode;
			this.objectReplacementMap = objectReplacementMap;
		}

		@Override
		public RecordEventsProcessor create(PrintStream out) {
			return new MethodCallToCodeRecordEventsProcessor(objToCode, objectReplacementMap, out);
		}
	}

	private ObjectToCodeGenerator objToCode;
	private ObjectReplacementMap objectReplacementMap; 
	private PrintStream out;
	
	//-------------------------------------------------------------------------

	public MethodCallToCodeRecordEventsProcessor(
			ObjectToCodeGenerator objToCode,
			ObjectReplacementMap objectReplacementMap,
			PrintStream out) {
		this.objToCode = objToCode;
		this.objectReplacementMap = objectReplacementMap;
		this.out = out;
	}

	// -------------------------------------------------------------------------
	
	@Override
	public boolean needEventObjectData() {
		return true;
	}

	@Override
	public void processEvent(RecordEventSummary event, Object eventData) {
		boolean isRequest = event.getEventSubType().equals("request");
		
		if (isRequest) {
			EventMethodRequestData reqData = (EventMethodRequestData) eventData;
			Object expr = reqData.getExpr();
			Method meth = reqData.getMethod();
			Object[] args = reqData.getArguments();
			
			out.println("{ // call eventId:" + event.getEventId() + " \"expr." + meth.getName() + "(...);\"");
			String exprName = prepareGenerateObj(meth.getReturnType(), expr, "expr");
			String[] argNames = null;
			int len = (args != null)? args.length : 0;
			if (len != 0) {
				argNames = new String[len];
				Class<?>[] parameterTypes = meth.getParameterTypes();
				for (int i = 0; i < len; i++) {
					argNames[i] = prepareGenerateObj(parameterTypes[i], args[i], "arg" + i);
				}
			}

			// out.println("// ... args for \"expr." + meth.getName() + "(...);\"");
			StringBuilder sb = new StringBuilder();
			sb.append(exprName);
			sb.append(".");
			sb.append(meth.getName());
			sb.append("(");
			if (len != 0) {
				for (int i = 0; i < len; i++) {
					sb.append(argNames[i]);
					if (i + 1 < len) {
						sb.append(", ");
					}
				}
			}			
			sb.append(");");

			out.println(sb.toString());
			
			out.println("} // end call eventId:" + event.getEventId());
			
		} else {
			// EventMethodResponseData respData = (EventMethodResponseData) eventData;
			// ignore real result in current impl...
			// Object result = respData.getResult();
			// String stmtsStr = objToCode.objToStmtsString(, "result");
			// out.print(stmtsStr);
			out.println("// result = ...");
		}
	}

	private String prepareGenerateObj(Class<?> declaredObjClass, Object obj, String exprVarName) {
		if (obj instanceof ObjectInstanceReplacement) {
			ObjectInstanceReplacement obj2 = (ObjectInstanceReplacement) obj;
			return obj2.getReplacedObjName();
		}
		obj = objectReplacementMap.checkReplace(obj);
		
		if (declaredObjClass != null && declaredObjClass.isPrimitive() 
				&& ReflectUtils.primitiveTypeToWrapperType(declaredObjClass) == obj.getClass() // check!
		) {
			// encode primitive value directly!
			return BeanASTPrettyPrinter.litteralToJava(obj);
		}
		StringBuilder stmtBuffer = new StringBuilder();
		String res = objToCode.objToStmtsString(declaredObjClass, obj, exprVarName, stmtBuffer);
		String stmtsText = stmtBuffer.toString();
		if (stmtsText.length() != 0) {
			out.println("// " + exprVarName + ":");
			out.print(stmtBuffer);
			// out.println("\n");
		}
		return res;
	}

	//-------------------------------------------------------------------------

	@Override
	public String toString() {
		return "MethodCallToCodeRecordEventsProcessor[..]";
	}
	
}
