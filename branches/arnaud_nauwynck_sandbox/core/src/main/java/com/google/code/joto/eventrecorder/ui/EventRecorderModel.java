package com.google.code.joto.eventrecorder.ui;

import java.beans.PropertyChangeListener;

import javax.swing.event.SwingPropertyChangeSupport;

import com.google.code.joto.eventrecorder.RecordEventStore;

/**
 * Swing model for event recorder
 */
public class EventRecorderModel {

	public static final String PROP_RECORDING_STATUS = "recordingStatus";
	
	public static enum RecordingStatus {
		RECORDING,
//		SUSPENDED_RECORDING,
		IDDLE
	}
	
	private SwingPropertyChangeSupport changeSupport =
		new SwingPropertyChangeSupport(this);

	private RecordingStatus recordingStatus = RecordingStatus.IDDLE;

	private RecordEventStore eventStore;
	
	private RecordEventSwingRedispatcher swingEventListenerAdapter;
	
	//-------------------------------------------------------------------------

	public EventRecorderModel(RecordEventStore eventStore) {
		this.eventStore = eventStore;
	}

	//-------------------------------------------------------------------------

	public void addPropertyChangeListener(PropertyChangeListener p) {
		changeSupport.addPropertyChangeListener(p);
	}
	public void removePropertyChangeListener(PropertyChangeListener p) {
		changeSupport.removePropertyChangeListener(p);
	}

	public RecordEventStore getRecordEventStore() {
		return eventStore;
	}
	
	
	public void startRecord() {
		if (isEnableStartRecord()) {
			// do start record..
			// TODO 
			
			
			setRecordingStatus(RecordingStatus.RECORDING);
		}
	}
	
	public boolean isEnableStartRecord() {
		return recordingStatus == RecordingStatus.IDDLE;
	}
	
	public void stopRecord() {
		if (isEnableStopRecord()) {
			// do stop record
			// TODO 
			
			setRecordingStatus(RecordingStatus.IDDLE);
		}
	}

	public boolean isEnableStopRecord() {
		return recordingStatus == RecordingStatus.RECORDING;
	}

	private void setRecordingStatus(RecordingStatus p) {
		if (p != recordingStatus) {
			RecordingStatus old = recordingStatus;
			this.recordingStatus = p;
			changeSupport.firePropertyChange(PROP_RECORDING_STATUS, old, p);
		}
	}



	
}
