package com.google.code.joto.eventrecorder;

import java.io.Serializable;
import java.util.List;

/**
 * 
 */
public abstract class RecordEventChange implements Serializable {
	
	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public abstract void accept(RecordEventChangeVisitor visitor);
	
	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class AddRecordEventStoreEvent extends RecordEventChange {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private RecordEventData eventData;

		public AddRecordEventStoreEvent(RecordEventData eventData) {
			super();
			this.eventData = eventData;
		}

		public void accept(RecordEventChangeVisitor visitor) {
			visitor.caseAddEvent(this);
		}
		
		public RecordEventData getEventData() {
			return eventData;
		}

		public RecordEventHandle getEventHandle() {
			return eventData.getEventHandle();
		}
		
	}

	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class TruncateRecordEventStoreEvent extends RecordEventChange {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private List<RecordEventHandle> truncateEventHandles;

		public TruncateRecordEventStoreEvent(List<RecordEventHandle> truncateEventHandles) {
			this.truncateEventHandles = truncateEventHandles;
		}

		public void accept(RecordEventChangeVisitor visitor) {
			visitor.caseTruncateEvent(this);
		}

		public List<RecordEventHandle> getTruncateEventHandles() {
			return truncateEventHandles;
		}
		
	}
	
	
}