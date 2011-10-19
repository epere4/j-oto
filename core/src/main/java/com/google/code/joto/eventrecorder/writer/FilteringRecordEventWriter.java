package com.google.code.joto.eventrecorder.writer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicate;

/**
 * proxy implementation of RecordEventWriter to add filtering
 * with global boolean, or List<RecordEventSummaryPredicate>
 */
public class FilteringRecordEventWriter extends AbstractRecordEventWriter implements RecordEventWriter {

	/** underlying proxy target object */
	private RecordEventWriter target;
	
	protected boolean enable = true;

	protected List<RecordEventSummaryPredicate> eventSummaryPredicates = new ArrayList<RecordEventSummaryPredicate>();
	
	// -------------------------------------------------------------------------
	
	public FilteringRecordEventWriter(RecordEventWriter target) {
		this.target = target;
	}

	// -------------------------------------------------------------------------

	@Override
	public void addEvent(RecordEventSummary info, Serializable objData, RecordEventWriterCallback callback) {
		if (enable == false || !isEnable(info)) {
			RecordEventData dummy = new RecordEventData(info, objData);			
			callback.onStore(dummy);
			return;
		}
		
		// delegate to underlying
		target.addEvent(info, objData, callback);
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

	public List<RecordEventSummaryPredicate> getEventSummaryPredicates() {
		return Collections.unmodifiableList(eventSummaryPredicates);
	}
	
	public void addEventSummaryPredicate(RecordEventSummaryPredicate p) {
		List<RecordEventSummaryPredicate> old = new ArrayList<RecordEventSummaryPredicate>(eventSummaryPredicates);
		eventSummaryPredicates.add(p);
		changeSupport.firePropertyChange("eventSummaryPredicates", old, eventSummaryPredicates);
	}
	
	public void removeEventSummaryPredicate(RecordEventSummaryPredicate p) {
		List<RecordEventSummaryPredicate> old = new ArrayList<RecordEventSummaryPredicate>(eventSummaryPredicates);
		eventSummaryPredicates.add(p);
		changeSupport.firePropertyChange("eventSummaryPredicates", old, eventSummaryPredicates);
	}

	
	public boolean isEnable(RecordEventSummary eventInfo) {
		if (eventSummaryPredicates != null && !eventSummaryPredicates.isEmpty()) {
			for(RecordEventSummaryPredicate pred : eventSummaryPredicates) {
				if (!pred.evaluate(eventInfo)) {
					return false;
				}
			}
		}
		return true;
	}

	
}
