package com.google.code.joto.eventrecorder.writer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.google.code.joto.eventrecorder.RecordEventStore;

/**
 *
 */
public abstract class AbstractRecordEventWriter implements RecordEventWriter {

	protected RecordEventStore eventStore;

	protected boolean enable = true;

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this); 
	
	// -------------------------------------------------------------------------
	
	public AbstractRecordEventWriter(RecordEventStore eventStore) {
		this.eventStore = eventStore;
	}

	// -------------------------------------------------------------------------

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean p) {
		if (p != enable) {
			boolean oldValue = enable;
			this.enable = p;
			changeSupport.firePropertyChange("enable", oldValue, p);
		}
	}

}
