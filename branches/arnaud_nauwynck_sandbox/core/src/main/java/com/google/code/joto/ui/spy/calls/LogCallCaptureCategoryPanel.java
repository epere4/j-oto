package com.google.code.joto.ui.spy.calls;

import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.capture.RecordEventsCaptureCategoryPanel;

/**
 *
 */
public class LogCallCaptureCategoryPanel extends RecordEventsCaptureCategoryPanel {

	public static final String LOGS_CAPTURE_CATEGORY = "logs";

	// ------------------------------------------------------------------------
	
	public LogCallCaptureCategoryPanel(JotoContext jotoContext) {
		super(jotoContext, LOGS_CAPTURE_CATEGORY);
	}

	// ------------------------------------------------------------------------
	
	
}
