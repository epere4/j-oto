package com.google.code.joto.eventrecorder.ui;

import java.util.List;

import javax.swing.SwingUtilities;

import com.google.code.joto.eventrecorder.RecordEventChange;
import com.google.code.joto.eventrecorder.RecordEventListener;

/**
 * adapter to redispatch RecordEvent to a swing listener (within the swing EDT) 
 */
public class RecordEventSwingRedispatcher implements RecordEventListener {

	private RecordEventListener target;
	
	//-------------------------------------------------------------------------

	public RecordEventSwingRedispatcher(RecordEventListener target) {
		this.target = target;
	}

	//-------------------------------------------------------------------------
	
	public void onEvent(final RecordEventChange event) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				target.onEvent(event);
			}
		});
	}

	public void onEvents(final List<RecordEventChange> events) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				target.onEvents(events);
			}
		});
	}
	
}
