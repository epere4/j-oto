package com.google.code.joto.eventrecorder.writer;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventSummary;

public interface RecordEventWriterCallback {

	public void onStore(RecordEventData stored);
	
	// -------------------------------------------------------------------------
	
	public static class CorrelatedEventSetterCallback implements RecordEventWriterCallback {

		private RecordEventSummary eventToFill;
		
		public CorrelatedEventSetterCallback(RecordEventSummary eventToFill) {
			super();
			this.eventToFill = eventToFill;
		}

		@Override
		public void onStore(RecordEventData event) {
			eventToFill.setCorrelatedEventId(event.getEventId());
		}
		
	}
}
