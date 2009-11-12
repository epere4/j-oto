package com.google.code.joto.eventrecorder.ui;

import javax.swing.table.AbstractTableModel;

import com.google.code.joto.eventrecorder.DefaultVisitorRecordEventListener;
import com.google.code.joto.eventrecorder.RecordEventChangeVisitor;
import com.google.code.joto.eventrecorder.RecordEventHandle;
import com.google.code.joto.eventrecorder.RecordEventListener;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventChange.TruncateRecordEventStoreEvent;

/**
 *
 */
public class RecordEventStoreTableModel extends AbstractTableModel {

	/** */
	private static final long serialVersionUID = 1L;
	
	private RecordEventStore eventStore;
	
	private RecordEventChangeVisitor eventHandler = new InnerRecordEventChangeVisitor();
	private RecordEventListener eventListener = new DefaultVisitorRecordEventListener(eventHandler);
	
	//-------------------------------------------------------------------------

	public RecordEventStoreTableModel(RecordEventStore eventStore) {
		this.eventStore = eventStore;
		eventStore.addRecordEventListener(eventListener);
	}

	//-------------------------------------------------------------------------
	
	public int getColumnCount() {
		return 7;
	}

	public int getRowCount() {
		return eventStore.getEvents().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		RecordEventHandle rowEvent = eventStore.getEvents().get(rowIndex);
		switch(columnIndex) {
		case 0: return rowEvent.getEventId();
		case 1: return rowEvent.getEventDate();
		case 2: return rowEvent.getEventType();
		case 3: return rowEvent.getEventSubType();
		case 4: return rowEvent.getEventMethodName();
		case 5: return rowEvent.getEventMethodDetail();
		case 6: return rowEvent.getObjectDataFilePosition();
		default: return null;
		}
	}

	// -------------------------------------------------------------------------
	
	private class InnerRecordEventChangeVisitor implements RecordEventChangeVisitor {

		public void caseAddEvent(AddRecordEventStoreEvent p) {
			int len = eventStore.getEvents().size();
			RecordEventStoreTableModel.this.fireTableRowsInserted(len, len);
		}

		public void caseTruncateEvent(TruncateRecordEventStoreEvent p) {
			RecordEventStoreTableModel.this.fireTableDataChanged();
		}

	}

}
