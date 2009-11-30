package com.google.code.joto.eventrecorder.writer;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 *
 */
public interface RecordEventWriter {

	public void addPropertyChangeListener(PropertyChangeListener listener);
	public void removePropertyChangeListener(PropertyChangeListener listener);

	public boolean isEnable();
	public void setEnable(boolean enable);

	public void addEvent(RecordEventSummary info,
			Serializable objData, 
			RecordEventWriterCallback callback);

}