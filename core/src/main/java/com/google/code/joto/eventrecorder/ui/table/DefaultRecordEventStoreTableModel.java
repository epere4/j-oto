package com.google.code.joto.eventrecorder.ui.table;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.ArrayList2;

/**
 * swing TableModel implementation for List<RecordEventSummary>
 */
public class DefaultRecordEventStoreTableModel extends AbstractTableModel {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	/** in-memory event rows ... similar to DefaultTableModel.dataVector  */
	private ArrayList2<RecordEventSummary> eventRows = new ArrayList2<RecordEventSummary>();

	// ------------------------------------------------------------------------

	public DefaultRecordEventStoreTableModel() {
	}
	
	// ------------------------------------------------------------------------
	
	public List<RecordEventSummary> getEventRows() {
		return eventRows;
	}

	public RecordEventSummary getEventRow(int row) {
		return (RecordEventSummary) eventRows.get(row);
	}
	
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
		RecordEventSummary rowEvent = getEventRow(rowIndex);
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

	// ------------------------------------------------------------------------
	
	public void addEventRow(RecordEventSummary row) {
		int index = eventRows.size(); 
		eventRows.add(index, row); 
	    fireTableRowsInserted(index, index);
	}
	
	public void removeRows(List<RecordEventSummary> rows) {
		eventRows.removeAll(rows);
		fireTableDataChanged();
	}

	public void clearEventRows() {
		eventRows.clear();
		fireTableDataChanged();
	}

	public void setRowElts(List<RecordEventSummary> elts) {
		eventRows.clear();
		eventRows.addAll(elts);
		fireTableDataChanged();
	}

	public void truncateEventRows(int maxEventRows) {
		int truncatedLen = eventRows.truncateHeadForMaxRows(maxEventRows);
		if (truncatedLen != 0) {
			fireTableRowsDeleted(0, truncatedLen);
		}
	}

}
