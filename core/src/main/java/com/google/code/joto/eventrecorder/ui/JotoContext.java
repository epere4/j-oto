package com.google.code.joto.eventrecorder.ui;

import java.beans.PropertyChangeListener;

import javax.swing.event.SwingPropertyChangeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.JotoConfig;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.ext.calls.ObjectReplacementMap;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.eventrecorder.writer.FilteringRecordEventWriter;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;

/**
 *
 */
public class JotoContext {

	public static final String PROP_RECORDING_STATUS = "recordingStatus";
	
	public static enum RecordingStatus {
		RECORDING,
		IDDLE
	}
	
	private static Logger log = LoggerFactory.getLogger(JotoContext.class);
	
	// ------------------------------------------------------------------------
	
	private JotoConfig config;

	private RecordEventStore eventStore;

	
	private SwingPropertyChangeSupport changeSupport = new SwingPropertyChangeSupport(this);

	private RecordingStatus recordingStatus = RecordingStatus.IDDLE;

	
	protected FilteringRecordEventWriter methodCallsFilteringEventWriter;
	protected FilteringRecordEventWriter logsFilteringEventWriter;
	protected FilteringRecordEventWriter awtEventSpyFilteringEventWriter;

	protected ObjectReplacementMap objReplMap = new ObjectReplacementMap();
	
	// ------------------------------------------------------------------------
	
	public JotoContext(JotoConfig config, RecordEventStore eventStore) {
		this.config = (config != null)? config : new JotoConfig();
		this.eventStore = (eventStore != null)? eventStore : this.config.getEventStoreFactory().create();

		RecordEventWriter asyncEventWriter = eventStore.getAsyncEventWriter();
		methodCallsFilteringEventWriter = new FilteringRecordEventWriter(asyncEventWriter);
		logsFilteringEventWriter = new FilteringRecordEventWriter(asyncEventWriter);
		awtEventSpyFilteringEventWriter = new FilteringRecordEventWriter(asyncEventWriter);

	}

	/** helper constructor, for test */
	public JotoContext() {
		this(new JotoConfig(), new DefaultMemoryRecordEventStore());
	}

	// ------------------------------------------------------------------------
	
	public void addPropertyChangeListener(PropertyChangeListener p) {
		changeSupport.addPropertyChangeListener(p);
	}
	public void removePropertyChangeListener(PropertyChangeListener p) {
		changeSupport.removePropertyChangeListener(p);
	}

	public RecordEventStore getEventStore() {
		return eventStore;
	}

	public JotoConfig getConfig() {
		return config;
	}

	public void setConfig(JotoConfig p) {
		JotoConfig old = config;
		this.config = p;
		changeSupport.firePropertyChange("config", old, p);
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
			log.info("setRecordingStatus " + p);
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

	public ObjectReplacementMap getObjReplMap() {
		return objReplMap;
	}

	public void setObjReplMap(ObjectReplacementMap p) {
		this.objReplMap = p;
	}
	

}
