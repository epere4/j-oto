package com.google.code.joto.eventrecorder.ui;

import java.beans.PropertyChangeListener;

import javax.swing.event.SwingPropertyChangeSupport;

import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.eventrecorder.writer.FilteringRecordEventWriter;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;

/**
 *
 */
public class JotoContext {

	private RecordEventStore eventStore;


	public static final String PROP_RECORDING_STATUS = "recordingStatus";
	
	public static enum RecordingStatus {
		RECORDING,
//		SUSPENDED_RECORDING,
		IDDLE
	}
	
	private SwingPropertyChangeSupport changeSupport =
		new SwingPropertyChangeSupport(this);

	private RecordingStatus recordingStatus = RecordingStatus.IDDLE;

	
	protected FilteringRecordEventWriter methodCallsFilteringEventWriter;
	protected FilteringRecordEventWriter logsFilteringEventWriter;
	protected FilteringRecordEventWriter awtEventSpyFilteringEventWriter;

	
	// ------------------------------------------------------------------------
	
	public JotoContext(RecordEventStore eventStore) {
		this.eventStore = eventStore;

		RecordEventWriter asyncEventWriter = eventStore.getAsyncEventWriter();
		methodCallsFilteringEventWriter = new FilteringRecordEventWriter(asyncEventWriter);
		logsFilteringEventWriter = new FilteringRecordEventWriter(asyncEventWriter);
		awtEventSpyFilteringEventWriter = new FilteringRecordEventWriter(asyncEventWriter);

	}

	/** helper constructor, for test */
	public JotoContext() {
		this(new DefaultMemoryRecordEventStore());
	}

	// ------------------------------------------------------------------------

	public RecordEventStore getEventStore() {
		return eventStore;
	}
	

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

	public FilteringRecordEventWriter getMethodCallsFilteringEventWriter() {
		return methodCallsFilteringEventWriter;
	}

	public void setMethodCallsFilteringEventWriter(FilteringRecordEventWriter p) {
		this.methodCallsFilteringEventWriter = p;
	}

	public FilteringRecordEventWriter getLogsFilteringEventWriter() {
		return logsFilteringEventWriter;
	}

	public void setLogsFilteringEventWriter(FilteringRecordEventWriter p) {
		this.logsFilteringEventWriter = p;
	}

	public FilteringRecordEventWriter getAwtEventSpyFilteringEventWriter() {
		return awtEventSpyFilteringEventWriter;
	}

	public void setAwtEventSpyFilteringEventWriter(FilteringRecordEventWriter p) {
		this.awtEventSpyFilteringEventWriter = p;
	}

	

}
