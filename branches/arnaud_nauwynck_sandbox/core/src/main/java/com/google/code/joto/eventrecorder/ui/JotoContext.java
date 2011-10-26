package com.google.code.joto.eventrecorder.ui;

import com.google.code.joto.JotoConfig;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.ext.calls.ObjectReplacementMap;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.eventrecorder.ui.filter.FilteringRecordEventWriterModel;
import com.google.code.joto.eventrecorder.writer.FilteringRecordEventWriter;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.SwingPropertyChangeSupport;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

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

	private FilteringRecordEventWriterModel captureFiltersWriterModel;

    protected Map<String,FilteringRecordEventWriterModel> filteringEventWriterModelCategories = new HashMap<String,FilteringRecordEventWriterModel>();

	protected ObjectReplacementMap objReplMap = new ObjectReplacementMap();
	
	// ------------------------------------------------------------------------
	
	public JotoContext(JotoConfig optConfig, RecordEventStore optEventStore) {
		this.config = (optConfig != null)? optConfig : new JotoConfig();
		this.eventStore = (optEventStore != null)? optEventStore : this.config.getEventStoreFactory().create();
		
		
		RecordEventWriter asyncEventWriter = eventStore.getAsyncEventWriter();

		this.captureFiltersWriterModel = new FilteringRecordEventWriterModel(asyncEventWriter);
		captureFiltersWriterModel.setName("jotoContext.captureFiltersWriterModel");
		captureFiltersWriterModel.setOwner(this);
		captureFiltersWriterModel.getResultFilteringEventWriter().setOwner(captureFiltersWriterModel);
		
		getOrCreateFilteringEventWriterModelCategory("methodCall");
//        getOrCreateFilteringEventWriterModelCategory("logs");
//        getOrCreateFilteringEventWriterModelCategory("AWTSpy");

		eventStore.open("rw");		
	}

	
	/** helper constructor, for test */
    public JotoContext(JotoConfig optConfig) {
        this(optConfig, null);
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

    public RecordEventWriter getEventWriter() {
        return eventStore.getEventWriter();
    }

    public RecordEventWriter getUnfilteredAsyncEventWriter() {
	    return eventStore.getAsyncEventWriter();
	}

    public RecordEventWriter getAsyncEventWriter() {
        return captureFiltersWriterModel.getResultFilteringEventWriter();
    }

    public FilteringRecordEventWriterModel getCaptureFiltersWriterModel() {
        return captureFiltersWriterModel;
    }

	public FilteringRecordEventWriterModel getOrCreateFilteringEventWriterModelCategory(String name) {
	    FilteringRecordEventWriterModel res = filteringEventWriterModelCategories.get(name);
	    if (res == null) {
	        FilteringRecordEventWriter mainAsyncEventWriter = captureFiltersWriterModel.getResultFilteringEventWriter();
	        res = new FilteringRecordEventWriterModel(mainAsyncEventWriter);
	        
	        boolean debugOwner = true;
	        if (debugOwner) {
	            res.setOwner(this);
                res.setName(name);
                
	            FilteringRecordEventWriter resFilter = res.getResultFilteringEventWriter();
    	        resFilter.setOwner(res);
    	        resFilter.setName(name);
	        }
	        
	        filteringEventWriterModelCategories.put(name, res);
	    }
	    return res;
	}

	public FilteringRecordEventWriter getOrCreateFilteringEventWriterCategory(String name) {
	    FilteringRecordEventWriterModel tmpres = getOrCreateFilteringEventWriterModelCategory(name);
	    return tmpres.getResultFilteringEventWriter();
	}

	/** helper method for getOrCreateFilteringEventWriterCategory("methodCall") */
	public FilteringRecordEventWriter getMethodCallEventWriterCategory() {
	    return getOrCreateFilteringEventWriterCategory("methodCall");
	}

	
    public ObjectReplacementMap getObjReplMap() {
		return objReplMap;
	}

	public void setObjReplMap(ObjectReplacementMap p) {
		this.objReplMap = p;
	}
	

}
