package com.google.code.joto.eventrecorder;

public interface RecordEventStoreCallback {

	public void onStore(RecordEventData stored);
	
	// -------------------------------------------------------------------------
	
	public static class CorrelatedEventSetterCallback implements RecordEventStoreCallback {

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
