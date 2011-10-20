package com.google.code.joto.eventrecorder.ui.filter;

import java.io.File;

import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicate;

/**
 * 
 */
public class RecordEventFilterItem {

	private String name;

	private String description;

	private File persistentFile;

	private RecordEventSummaryPredicate eventPredicate;

	private String eventIdPredicateDescription;
	private String eventDatePredicateDescription;
	private String threadNamePredicateDescription;
	private String eventTypePredicateDescription;
	private String eventSubTypePredicateDescription;
	private String eventClassNamePredicateDescription;
	private String eventMethodNamePredicateDescription;
	private String eventMethodDetailPredicateDescription;
	private String correlatedEventIdPredicateDescription;

	// ------------------------------------------------------------------------

	public RecordEventFilterItem() {
	}

	// ------------------------------------------------------------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String p) {
		this.description = p;
	}

	public File getPersistentFile() {
		return persistentFile;
	}

	public void setPersistentFile(File p) {
		this.persistentFile = p;
	}

	public RecordEventSummaryPredicate getEventPredicate() {
		return eventPredicate;
	}

	public void setEventPredicate(RecordEventSummaryPredicate p) {
		this.eventPredicate = p;
	}

	public String getEventIdPredicateDescription() {
		return eventIdPredicateDescription;
	}

	public void setEventIdPredicateDescription(String p) {
		this.eventIdPredicateDescription = p;
	}

	public String getEventDatePredicateDescription() {
		return eventDatePredicateDescription;
	}

	public void setEventDatePredicateDescription(String p) {
		this.eventDatePredicateDescription = p;
	}

	public String getThreadNamePredicateDescription() {
		return threadNamePredicateDescription;
	}

	public void setThreadNamePredicateDescription(String p) {
		this.threadNamePredicateDescription = p;
	}

	public String getEventTypePredicateDescription() {
		return eventTypePredicateDescription;
	}

	public void setEventTypePredicateDescription(String p) {
		this.eventTypePredicateDescription = p;
	}

	public String getEventSubTypePredicateDescription() {
		return eventSubTypePredicateDescription;
	}

	public void setEventSubTypePredicateDescription(String p) {
		this.eventSubTypePredicateDescription = p;
	}

	public String getEventClassNamePredicateDescription() {
		return eventClassNamePredicateDescription;
	}

	public void setEventClassNamePredicateDescription(String p) {
		this.eventClassNamePredicateDescription = p;
	}

	public String getEventMethodNamePredicateDescription() {
		return eventMethodNamePredicateDescription;
	}

	public void setEventMethodNamePredicateDescription(String p) {
		this.eventMethodNamePredicateDescription = p;
	}

	public String getEventMethodDetailPredicateDescription() {
		return eventMethodDetailPredicateDescription;
	}

	public void setEventMethodDetailPredicateDescription(String p) {
		this.eventMethodDetailPredicateDescription = p;
	}

	public String getCorrelatedEventIdPredicateDescription() {
		return correlatedEventIdPredicateDescription;
	}

	public void setCorrelatedEventIdPredicateDescription(String p) {
		this.correlatedEventIdPredicateDescription = p;
	}

	
	public void set(RecordEventFilterItem src) {
		this.name = src.name;
		this.description = src.description;
		this.persistentFile = src.persistentFile;

		this.eventPredicate = src.eventPredicate;

		this.eventIdPredicateDescription = src.eventIdPredicateDescription;
		this.eventDatePredicateDescription = src.eventDatePredicateDescription;
		this.threadNamePredicateDescription = src.threadNamePredicateDescription;
		this.eventTypePredicateDescription = src.eventTypePredicateDescription;
		this.eventSubTypePredicateDescription = src.eventSubTypePredicateDescription;
		this.eventClassNamePredicateDescription = src.eventClassNamePredicateDescription; 
		this.eventMethodNamePredicateDescription = src.eventMethodNamePredicateDescription;
		this.eventMethodDetailPredicateDescription = src.eventMethodDetailPredicateDescription;
		this.correlatedEventIdPredicateDescription = src.correlatedEventIdPredicateDescription;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "RecordEventFilterItem [" 
				+ "name=" + name 
				+ ", description=" + description 
				+ ", persistentFile=" + persistentFile 
				+ "]";
	}

	
}
