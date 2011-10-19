package com.google.code.joto.eventrecorder.ui;

import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.google.code.joto.eventrecorder.DefaultVisitorRecordEventListener;
import com.google.code.joto.eventrecorder.RecordEventChangeVisitor;
import com.google.code.joto.eventrecorder.RecordEventListener;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.StartRecordingEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.StopRecordingEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.TruncateRecordEventStoreEvent;
import com.google.code.joto.util.ArrayList2;

/**
 *
 */
public class RecordEventStoreTableModel extends AbstractTableModel {

	/** */
	private static final long serialVersionUID = 1L;
	
	private RecordEventStore eventStore;
	
	/** local copy of displayed events, receoved from underlying eventStore 
	 * can be truncated / cleared / reloaded from eventStore 
	 */
	private final ArrayList2<RecordEventSummary> eventRows = new ArrayList2<RecordEventSummary>();
	
	private int maxEventRows = -1;
	
	private RecordEventChangeVisitor eventHandler = new InnerRecordEventChangeVisitor();
	private RecordEventListener eventListener = new DefaultVisitorRecordEventListener(eventHandler);
	
	//-------------------------------------------------------------------------

	public RecordEventStoreTableModel(RecordEventStore eventStore) {
		this.eventStore = eventStore;
		// TODO should check to wrap listener for SwingUtilities.invokeLater?
		int availableFirstEventId = eventStore.getFirstEventId();
		int availableLastEventId = eventStore.getLastEventId();
		int len = availableLastEventId - availableFirstEventId;
		int requestFromEventId = availableFirstEventId; 
		if (maxEventRows != -1 && len > maxEventRows) {
			len = maxEventRows;
			requestFromEventId = availableLastEventId - len; 
		}
		reloadEventRows(requestFromEventId, -1);
		eventStore.addRecordEventListener(eventListener);
	}

	//-------------------------------------------------------------------------

	/** implements TableModel */
	public int getColumnCount() {
		return 7;
	}

	/** implements TableModel */
	public int getRowCount() {
		return eventRows.size();
	}

	/** implements TableModel */
	public Object getValueAt(int rowIndex, int columnIndex) {
		RecordEventSummary rowEvent = eventRows.get(rowIndex);
		switch(columnIndex) {
		case 0: return rowEvent.getEventId();
		case 1: return rowEvent.getEventDate();
		case 2: return rowEvent.getEventType();
		case 3: return rowEvent.getEventSubType();
		case 4: return rowEvent.getEventClassName();
		case 5: return rowEvent.getEventMethodName();
		case 6: return rowEvent.getEventMethodDetail();
		case 7: return rowEvent.getInternalEventStoreDataAddress();
		default: return null;
		}
	}

	/** callback from inner eventStore listener */
	private void onAddEvent(AddRecordEventStoreEvent p) {
		eventRows.add(p.getEventSummary());
		int len = eventRows.size();
		fireTableRowsInserted(len, len);
	}

	
	
	/** callback from inner eventStore listener */
	private void onEventStoreTruncate(TruncateRecordEventStoreEvent p) {
		List<RecordEventSummary> optTruncateEventSummaries = p.getOptTruncateEventSummaries();
		if (optTruncateEventSummaries != null) {
			eventRows.removeAll(optTruncateEventSummaries);
		} else {
			// truncated events not available... scan from range eventId
			int fromEventId = p.getFromEventId();
			int toEventId = p.getFromEventId();
			for (Iterator<RecordEventSummary> iter = eventRows.iterator(); iter.hasNext();) {
				RecordEventSummary e = iter.next();
				int eId = e.getEventId();
				if (eId < fromEventId) {
					// strange?.. should have been removed already...
					iter.remove();
				} else if (fromEventId <= eId && eId < toEventId) {
					iter.remove();
				} else {
					// reached end of truncation
					break;
				}
			}
		}
		fireTableDataChanged();
	}

	// -------------------------------------------------------------------------

	public int getMaxEventRows() {
		return maxEventRows;
	}

	public void setMaxEventRows(int p) {
		this.maxEventRows = p;
		int truncatedLen = eventRows.truncateHeadForMaxRows(maxEventRows);
		if (truncatedLen != 0) {
			fireTableRowsDeleted(0, truncatedLen);
		}
	}
		
	public RecordEventSummary getEventRow(int row) {
		return eventRows.get(row);
	}

	public void clearEventRows() {
		eventRows.clear();
		fireTableDataChanged();
	}

	public void reloadEventRows(int fromEventId, int toEventId) {
		// TODO use SwingWorker ... 
		List<RecordEventSummary> newEventRows = eventStore.getEvents(fromEventId, toEventId);
		eventRows.clear();
		eventRows.addAll(newEventRows);
		eventRows.truncateHeadForMaxRows(maxEventRows);
		fireTableDataChanged();
	}


	// -------------------------------------------------------------------------
	
	private class InnerRecordEventChangeVisitor implements RecordEventChangeVisitor {

		public void caseAddEvent(AddRecordEventStoreEvent p) {
			onAddEvent(p);
		}

		public void caseTruncateEvent(TruncateRecordEventStoreEvent p) {
			onEventStoreTruncate(p);
		}

		@Override
		public void caseStartRecording(StartRecordingEvent p) {
			// do nothing?
		}

		@Override
		public void caseStopRecording(StopRecordingEvent p) {
			// do nothing?
		}
		
	}

}
