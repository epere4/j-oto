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

		private RecordEventHandle eventHandle;

		public AddRecordEventStoreEvent(RecordEventHandle eventHandle) {
			super();
			this.eventHandle = eventHandle;
		}

		public void accept(RecordEventChangeVisitor visitor) {
			visitor.caseAddEvent(this);
		}
		
		public RecordEventHandle getEventHandle() {
			return eventHandle;
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