package com.google.code.joto.eventrecorder;

import java.util.List;

/**
 *
 */
public interface RecordEventListener {

	public void onEvent(RecordEventChange event);
	public void onEvents(List<RecordEventChange> event);
	
}
