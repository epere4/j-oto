package com.google.code.joto.eventrecorder.predicate;

import java.io.Serializable;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.collections.functors.OrPredicate;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.thoughtworks.xstream.XStream;

/**
 * Utility Predicate classes for RecortEventSummary 
 */
public class RecortEventSummaryPredicateUtils {

	public static XStream createDefaultPredicateXStream() {
		XStream res = new XStream();
		registerDefaultPredicateXStreamAlias(res);
		return res;
	}
	
	public static void registerDefaultPredicateXStreamAlias(XStream res) {
		res.alias("and", AndPredicate.class);
		res.alias("or", OrPredicate.class);
		res.alias("not", NotPredicate.class);
		res.alias("equal", EqualPredicate.class);
		res.alias("MatchesEvent", DefaultEventTypeRecordEventSummaryPredicate.class);
		res.alias("MatchesEventType", EventTypeRecordEventSummaryPredicate.class);
	}
	
	public static RecordEventSummaryPredicate byFields(
			Predicate eventIdPredicate, 
			Predicate eventDatePredicate,
			Predicate threadNamePredicate, 
			Predicate eventTypePredicate,
			Predicate eventSubTypePredicate,
			Predicate eventClassNamePredicate,
			Predicate eventMethodNamePredicate,
			Predicate eventMethodDetailPredicate,
			Predicate correlatedEventIdPredicate) {
		return new DefaultEventTypeRecordEventSummaryPredicate(eventIdPredicate, eventDatePredicate, 
				threadNamePredicate, eventTypePredicate, eventSubTypePredicate, 
				eventClassNamePredicate, eventMethodNamePredicate, 
				eventMethodDetailPredicate, correlatedEventIdPredicate);
	}
	
	public static RecordEventSummaryPredicate byEventType(Predicate/*<String>*/ eventTypePredicate) {
		return new EventTypeRecordEventSummaryPredicate(eventTypePredicate);
	}

	public static RecordEventSummaryPredicate byEventType(String eventType) {
		return new EventTypeRecordEventSummaryPredicate(PredicateUtils.equalPredicate(eventType));
	}

	// ------------------------------------------------------------------------
	
	public static abstract class AbstractRecordEventSummaryPredicate implements RecordEventSummaryPredicate, Serializable {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		@Override
		public boolean evaluate(Object obj) {
			if (obj == null || !(obj instanceof RecordEventSummary)) return false;
			else return evaluate((RecordEventSummary) obj);
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * default Predicate for RecordEventSummary with (optional) sub-predicate for each field  
	 */
	public static class DefaultEventTypeRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final Predicate/*<int>*/ eventIdPredicate;
		private final Predicate/*<Date>*/ eventDatePredicate;
		private final Predicate/*<String>*/ threadNamePredicate;
		private final Predicate/*<String>*/ eventTypePredicate;
		private final Predicate/*<String>*/ eventSubTypePredicate;
		private final Predicate/*<String>*/ eventClassNamePredicate;
		private final Predicate/*<String>*/ eventMethodNamePredicate;
		private final Predicate/*<String>*/ eventMethodDetailPredicate;
		private final Predicate/*<int>*/ correlatedEventIdPredicate; 

		public DefaultEventTypeRecordEventSummaryPredicate(
				Predicate eventIdPredicate, 
				Predicate eventDatePredicate,
				Predicate threadNamePredicate, 
				Predicate eventTypePredicate,
				Predicate eventSubTypePredicate,
				Predicate eventClassNamePredicate,
				Predicate eventMethodNamePredicate,
				Predicate eventMethodDetailPredicate,
				Predicate correlatedEventIdPredicate) {
			super();
			this.eventIdPredicate = eventIdPredicate;
			this.eventDatePredicate = eventDatePredicate;
			this.threadNamePredicate = threadNamePredicate;
			this.eventTypePredicate = eventTypePredicate;
			this.eventSubTypePredicate = eventSubTypePredicate;
			this.eventClassNamePredicate = eventClassNamePredicate;
			this.eventMethodNamePredicate = eventMethodNamePredicate;
			this.eventMethodDetailPredicate = eventMethodDetailPredicate;
			this.correlatedEventIdPredicate = correlatedEventIdPredicate;
		}


		@Override
		public boolean evaluate(RecordEventSummary evt) {
			if (eventIdPredicate != null 
					&& !eventIdPredicate.evaluate(Integer.valueOf(evt.getEventId()))) {
				return false;
			}
			if (eventDatePredicate != null 
					&& !eventDatePredicate.evaluate(evt.getEventDate())) {
				return false;
			}
			if (threadNamePredicate != null 
					&& !threadNamePredicate.evaluate(evt.getThreadName())) {
				return false;
			}
			if (eventTypePredicate != null 
					&& !eventTypePredicate.evaluate(evt.getEventType())) {
				return false;
			}
			if (eventSubTypePredicate != null 
					&& !eventSubTypePredicate.evaluate(evt.getEventSubType())) {
				return false;
			}
			if (eventClassNamePredicate != null 
					&& !eventClassNamePredicate.evaluate(evt.getEventClassName())) {
				return false;
			}
			if (eventMethodNamePredicate != null 
					&& !eventMethodNamePredicate.evaluate(evt.getEventMethodName())) {
				return false;
			}
			if (eventMethodDetailPredicate != null 
					&& !eventMethodDetailPredicate.evaluate(evt.getEventMethodDetail())) {
				return false;
			}
			if (correlatedEventIdPredicate != null 
					&& !correlatedEventIdPredicate.evaluate(evt.getCorrelatedEventId())) {
				return false;
			}
			
			return true;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/**
	 * Predicate for <pre>event.getEventType() ... matches condition</pre>  
	 */
	public static class EventTypeRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final Predicate/*<String>*/ eventTypePredicate;
		
		public EventTypeRecordEventSummaryPredicate(Predicate eventTypePredicate) {
			super();
			this.eventTypePredicate = eventTypePredicate;
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			return eventTypePredicate.evaluate(evt.getEventType());
		}
		
	}
	
}
