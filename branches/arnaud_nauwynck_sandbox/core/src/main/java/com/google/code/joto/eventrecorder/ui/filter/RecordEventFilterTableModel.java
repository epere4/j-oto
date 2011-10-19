package com.google.code.joto.eventrecorder.ui.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicate;

/**
 * simple swing TableModel for RecordEventSummaryPredicate 
 *
 */
public class RecordEventFilterTableModel extends AbstractTableModel {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public static enum ColumnInfo {
		name("name", String.class, true),
		description("description", String.class, true),
		persistentFile("file", String.class, true), // use File.class ??
		
		eventPredicate("Pred", String.class, false),  // no cell renderer/editor... (RecordEventSummaryPredicate.class, false),

		eventIdPredicateDescription("Id ~~", String.class, true),
		eventDatePredicateDescription("Date ~~", String.class, true),
		threadNamePredicateDescription("ThreadName ~~", String.class, true),
		eventTypePredicateDescription("Type ~~", String.class, true),
		eventSubTypePredicateDescription("SubType ~~", String.class, true),
		eventClassNamePredicateDescription("ClassName ~~", String.class, true),
		eventMethodNamePredicateDescription("MethodName ~~", String.class, true),
		eventMethodDetailPredicateDescription("ClassDetail~~", String.class, true),
		correlatedEventIdPredicateDescription("CorrId ~~", String.class, true);

		private String columnName;
		private Class<?> columnClass;
		boolean isEditable;
		
		private ColumnInfo(String columnName, Class<?> columnClass, boolean isEditable) {
			this.columnName = columnName;
			this.columnClass = columnClass;
			this.isEditable = isEditable;
		}


		public String getColumnName() {
			return columnName;
		}

		public Class<?> getColumnClass() {
			return columnClass;
		}

		public boolean isEditable() {
			return isEditable;
		}

		public static ColumnInfo[] getSTD_COLS() {
			return STD_COLS;
		}




		private static ColumnInfo[] STD_COLS = ColumnInfo.values();

		public static ColumnInfo fromOrdinal(int i) {
			return STD_COLS[i];
		}
		
	}
	
	
	
	private List<RecordEventFilter> rows = new ArrayList<RecordEventFilter>();
	
	// ------------------------------------------------------------------------

	public RecordEventFilterTableModel() {
	}

	// ------------------------------------------------------------------------


	public RecordEventFilter getRow(int rowIndex) {
		if (rowIndex < 0 || rowIndex >= rows.size()) return null; // should not occur!
		return rows.get(rowIndex);
	}

	public void addRow(RecordEventFilter p) {
		rows.add(p);
	}
	
	// implements swing TableModel
	// ------------------------------------------------------------------------
	
	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return ColumnInfo.STD_COLS.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= rows.size()) return null; // should not occur!
		RecordEventFilter row = getRow(rowIndex);
		switch(ColumnInfo.fromOrdinal(columnIndex)) {
		case name: return row.getName();
		case description: return row.getDescription();
		case persistentFile: {
			File persistentFile = row.getPersistentFile();
			return (persistentFile != null)? persistentFile.getName() : "";
		}
		case eventPredicate: return row.getEventPredicate();

		case eventIdPredicateDescription: return row.getEventIdPredicateDescription();
		case eventDatePredicateDescription: return row.getEventDatePredicateDescription();
		case threadNamePredicateDescription: return row.getThreadNamePredicateDescription();
		case eventTypePredicateDescription: return row.getEventTypePredicateDescription();
		case eventSubTypePredicateDescription: return row.getEventSubTypePredicateDescription();
		case eventClassNamePredicateDescription: return row.getEventClassNamePredicateDescription();
		case eventMethodNamePredicateDescription: return row.getEventMethodNamePredicateDescription();
		case eventMethodDetailPredicateDescription: return row.getEventMethodDetailPredicateDescription();
		case correlatedEventIdPredicateDescription: return row.getCorrelatedEventIdPredicateDescription();
		default: return null;
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		ColumnInfo col = ColumnInfo.fromOrdinal(columnIndex);
		return col.getColumnName();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		ColumnInfo col = ColumnInfo.fromOrdinal(columnIndex);
		return col.getColumnClass();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		ColumnInfo col = ColumnInfo.fromOrdinal(columnIndex);
		return col.isEditable();
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= rows.size()) return; // should not occur!
		RecordEventFilter row = getRow(rowIndex);
		switch(ColumnInfo.fromOrdinal(columnIndex)) {
		case name: row.setName((String) value); break;
		case description: row.setDescription((String) value); break;
		case persistentFile: {
			File persistentFile = (value != null)? new File((String) value) : null; 
			row.setPersistentFile(persistentFile);
		} break;
		case eventPredicate: 
			// not editable .. return row.setEventPredicate(() value); 
			break;

		case eventIdPredicateDescription: row.setEventIdPredicateDescription((String) value); break;
		case eventDatePredicateDescription: row.setEventDatePredicateDescription((String) value); break;
		case threadNamePredicateDescription: row.setThreadNamePredicateDescription((String) value); break;
		case eventTypePredicateDescription: row.setEventTypePredicateDescription((String) value); break;
		case eventSubTypePredicateDescription: row.setEventSubTypePredicateDescription((String) value); break;
		case eventClassNamePredicateDescription: row.setEventClassNamePredicateDescription((String) value); break;
		case eventMethodNamePredicateDescription: row.setEventMethodNamePredicateDescription((String) value); break;
		case eventMethodDetailPredicateDescription: row.setEventMethodDetailPredicateDescription((String) value); break;
		case correlatedEventIdPredicateDescription: row.setCorrelatedEventIdPredicateDescription((String) value); break;
		default: 
			// should not occur
			break;
		}
	}
	
	
}
