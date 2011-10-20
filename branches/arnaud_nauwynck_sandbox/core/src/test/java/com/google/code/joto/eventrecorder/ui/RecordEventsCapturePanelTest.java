package com.google.code.joto.eventrecorder.ui;

import org.junit.Test;

import com.google.code.joto.util.io.ui.UiTestUtils;

public class RecordEventsCapturePanelTest {

	@Test
	public void testDoNothing() {
	}
	
	@Test
	public void testOpenClosePanel() {
		RecordEventsCapturePanel obj = new RecordEventsCapturePanel();
		UiTestUtils.showInFrame(obj.getJComponent());
	}
}
