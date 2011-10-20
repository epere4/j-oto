package com.google.code.joto.eventrecorder.ui.filter;

import org.junit.Test;

import com.google.code.joto.util.io.ui.UiTestUtils;

/**
 * JUnit test for RecordEventFilterTablePanel
 */
public class RecordEventFilterTablePanelTest {

	@Test
	public void testDoNothing() {
	}
	
	@Test
	public void openCloseEditorPane() throws Exception {
		RecordEventFilterItemEditor editor = new RecordEventFilterItemEditor();
		UiTestUtils.showInFrame(editor.getJComponent());
	}
	
//	@Test
	public void openCloseTablePane() throws Exception {
		RecordEventFilterItemTableModel tm = new RecordEventFilterItemTableModel();
		RecordEventFilterItem f1 = new RecordEventFilterItem();
		tm.addRow(f1);
		RecordEventFilterItem f2 = new RecordEventFilterItem();
		tm.addRow(f2);
		
		RecordEventFilterItemTablePanel tableView = new RecordEventFilterItemTablePanel(tm);
		
		UiTestUtils.showInFrame(tableView.getJComponent());
	}

}
