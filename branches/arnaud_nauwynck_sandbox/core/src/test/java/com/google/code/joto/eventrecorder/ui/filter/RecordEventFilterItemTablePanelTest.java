package com.google.code.joto.eventrecorder.ui.filter;

import org.junit.Test;

import com.google.code.joto.util.io.ui.UiTestUtils;

public class RecordEventFilterItemTablePanelTest {

	@Test
	public void testOpenClosePane() {
		RecordEventFilterItemTableModel model = new RecordEventFilterItemTableModel();
		RecordEventFilterItemTablePanel pane = new RecordEventFilterItemTablePanel(model);
		UiTestUtils.showInFrame(pane.getJComponent());
	}
}
