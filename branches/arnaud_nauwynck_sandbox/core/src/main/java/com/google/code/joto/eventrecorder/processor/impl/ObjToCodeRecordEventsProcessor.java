package com.google.code.joto.eventrecorder.processor.impl;

import java.io.PrintStream;

import com.google.code.joto.ObjectToCodeGenerator;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;

/**
 * Formatter for converting RecordEvent(s) -> String as Java "new/call" code, 
 * using ObjectToCodeGenerator
 */
public class ObjToCodeRecordEventsProcessor implements RecordEventsProcessor {

	public static class Factory implements RecordEventsProcessorFactory<PrintStream> {
		private ObjectToCodeGenerator objToCode;
		
		public Factory(ObjectToCodeGenerator objToCode) {
			super();
			this.objToCode = objToCode;
		}

		@Override
		public RecordEventsProcessor create(PrintStream out) {
			return new ObjToCodeRecordEventsProcessor(objToCode, out);
		}
	}

	private ObjectToCodeGenerator objToCode;
	private PrintStream out;
	
	//-------------------------------------------------------------------------

	public ObjToCodeRecordEventsProcessor(ObjectToCodeGenerator objToCode, PrintStream out) {
		this.objToCode = objToCode;
		this.out = out;
	}

	// -------------------------------------------------------------------------
	
	@Override
	public boolean needEventObjectData() {
		return true;
	}

	@Override
	public void processEvent(RecordEventSummary event, Object eventData) {
		out.print("meth:" + event.getEventMethodName() + "\n");
		out.print("\n");
		out.print("code: {\n\n");
		
		String stmtsStr = objToCode.objToStmtsString(eventData, "eventData");
		out.print(stmtsStr);
		
		out.print("\n} // code\n");
		
		out.print("\n");
	}

	//-------------------------------------------------------------------------

	@Override
	public String toString() {
		return "ObjToCodeRecordEventsProcessor[..]";
	}
	
}
