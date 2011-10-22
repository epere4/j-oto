package com.google.code.joto.eventrecorder.predicate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.collections.functors.OrPredicate;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.PatternsPredicate;
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
		res.alias("DefaultEventPredicate", DefaultEventTypeRecordEventSummaryPredicate.class);

		res.alias("WithTypeSubType", WithTypeSubTypeRecordEventSummaryPredicate.class);
		res.alias("WithClassMethodType", WithClassMethodRecordEventSummaryPredicate.class);
		
		res.alias("ClassMethodEquals", ClassMethodEqualsRecordEventSummaryPredicate.class);
		res.alias("TypeSubTypeEquals", TypeSubTypeEqualsRecordEventSummaryPredicate.class);
		
		res.alias("WithEventType", WithEventTypeRecordEventSummaryPredicate.class);
		res.alias("WithEventSubType", WithEventSubTypeRecordEventSummaryPredicate.class);
		res.alias("WithEventClassName", WithEventClassNameRecordEventSummaryPredicate.class);
		res.alias("WithEventMethodName", WithEventMethodNameRecordEventSummaryPredicate.class);
		res.alias("WithEventMethodDetail", WithEventMethodDetailRecordEventSummaryPredicate.class);
	}
	
	public static RecordEventSummaryPredicate withFields(
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
	
	public static RecordEventSummaryPredicate withEventType(Predicate/*<String>*/ predicate) {
		return new WithEventTypeRecordEventSummaryPredicate(predicate);
	}

	public static RecordEventSummaryPredicate withEventSubType(Predicate/*<String>*/ predicate) {
		return new WithEventSubTypeRecordEventSummaryPredicate(predicate);
	}

	public static RecordEventSummaryPredicate withEventClassName(Predicate/*<String>*/ predicate) {
		return new WithEventSubTypeRecordEventSummaryPredicate(predicate);
	}

	public static RecordEventSummaryPredicate withEventMethodName(Predicate/*<String>*/ predicate) {
		return new WithEventSubTypeRecordEventSummaryPredicate(predicate);
	}

	public static RecordEventSummaryPredicate withEventMethodDetail(Predicate/*<String>*/ predicate) {
		return new WithEventSubTypeRecordEventSummaryPredicate(predicate);
	}

	
	
	public static RecordEventSummaryPredicate withEventTypeEqual(String eventType) {
		return withEventType(PredicateUtils.equalPredicate(eventType));
	}

	public static RecordEventSummaryPredicate withEventSubTypeEqual(String eventType) {
		return withEventSubType(PredicateUtils.equalPredicate(eventType));
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
	 * Predicate for composing condition with <code>eventType</code> and <code>eventSubType</code>   
	 */
	public static class WithTypeSubTypeRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final Predicate/*<String>*/ eventTypePredicate;
		private final Predicate/*<String>*/ eventSubTypePredicate;

		public WithTypeSubTypeRecordEventSummaryPredicate(
				Predicate eventTypePredicate, Predicate eventSubTypePredicate) {
			super();
			this.eventTypePredicate = eventTypePredicate;
			this.eventSubTypePredicate = eventSubTypePredicate;
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			if (eventTypePredicate != null 
					&& !eventTypePredicate.evaluate(evt.getEventType())) {
				return false;
			}
			if (eventSubTypePredicate != null 
					&& !eventSubTypePredicate.evaluate(evt.getEventSubType())) {
				return false;
			}
			return true;
		}
		
	}

	/**
	 * Predicate with basic conditions on <code>eventType</code> and <code>eventSubType</code>   
	 */
	public static class TypeSubTypeEqualsRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final String eventTypeValue;
		private Collection<String> eventSubTypeIncludes;
		private Collection<String> eventSubTypeExcludes;

		/** transient, computed from eventSubTypeIncludes,eventSubTypeExcludes */
		transient private PatternsPredicate _cachedEventSubTypePatterns;
		
		public TypeSubTypeEqualsRecordEventSummaryPredicate(
				String eventTypeValue, 
				Collection<String> eventSubTypeIncludes, Collection<String> eventSubTypeExcludes) {
			super();
			this.eventTypeValue = eventTypeValue;
			this.eventSubTypeIncludes = eventSubTypeIncludes;
			this.eventSubTypeExcludes = eventSubTypeExcludes;
			this._cachedEventSubTypePatterns = PatternsPredicate.snewCompilePatterns(eventSubTypeIncludes, eventSubTypeExcludes);
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			if (eventTypeValue != null 
					&& !eventTypeValue.equals(evt.getEventType())) {
				return false;
			}
			if (_cachedEventSubTypePatterns == null) {
				this._cachedEventSubTypePatterns = PatternsPredicate.snewCompilePatterns(eventSubTypeIncludes, eventSubTypeExcludes);
			}
			if (_cachedEventSubTypePatterns != null 
					&& !_cachedEventSubTypePatterns.evaluate(evt.getEventSubType())) {
				return false;
			}
			return true;
		}
		
	}
	
	
	/**
	 * Predicate for composing condition with <code>ClassName</code>, <code>MethodName</code> and <code>MethodDetail</code>
	 */
	public static class WithClassMethodRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final Predicate/*<String>*/ eventClassNamePredicate;
		private final Predicate/*<String>*/ eventMethodNamePredicate;
		private final Predicate/*<String>*/ eventMethodDetailPredicate;

		public WithClassMethodRecordEventSummaryPredicate(
				Predicate eventClassNamePredicate,
				Predicate eventMethodNamePredicate,
				Predicate eventMethodDetailPredicate) {
			super();
			this.eventClassNamePredicate = eventClassNamePredicate;
			this.eventMethodNamePredicate = eventMethodNamePredicate;
			this.eventMethodDetailPredicate = eventMethodDetailPredicate;
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
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
			return true;
		}
		
	}
	

	/**
	 * Predicate with basic conditions on <code>ClassName</code>, <code>MethodName</code>
	 */
	public static class ClassMethodEqualsRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final String className;
		private final List<String> methodNameIncludes;
		private final List<String> methodNameExcludes;		
		
		/** transient, computed from methodNameIncludes,methodNameExcludes */
		transient private PatternsPredicate _cachedMethodNamePatterns;
		
		public ClassMethodEqualsRecordEventSummaryPredicate(
				String className,
				List<String> methodNameIncludes,
				List<String> methodNameExcludes) {
			super();
			this.className = className;
			this.methodNameIncludes = methodNameIncludes;
			this.methodNameExcludes = methodNameExcludes;
			this._cachedMethodNamePatterns = PatternsPredicate.snewCompilePatterns(methodNameIncludes, methodNameExcludes);
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			if (className != null 
					&& !className.equals(evt.getEventClassName())) {
				return false;
			}
			if (_cachedMethodNamePatterns == null) {
				this._cachedMethodNamePatterns = PatternsPredicate.snewCompilePatterns(methodNameIncludes, methodNameExcludes);
			}
			if (_cachedMethodNamePatterns != null 
					&& !_cachedMethodNamePatterns.evaluate(evt.getEventMethodName())) {
				return false;
			}
			return true;
		}
		
	}
	
	
	// ------------------------------------------------------------------------

	/**
	 * Predicate for composing condition with <pre>event.getEventType()</pre>  
	 */
	public static class WithEventTypeRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final Predicate/*<String>*/ eventTypePredicate;
		
		public WithEventTypeRecordEventSummaryPredicate(Predicate eventTypePredicate) {
			super();
			this.eventTypePredicate = eventTypePredicate;
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			return eventTypePredicate.evaluate(evt.getEventType());
		}
		
	}
	

	/**
	 * Predicate for composing with condition with <pre>event.getEventSubType()</pre>  
	 */
	public static class WithEventSubTypeRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final Predicate/*<String>*/ eventSubTypePredicate;
		
		public WithEventSubTypeRecordEventSummaryPredicate(Predicate eventSubTypePredicate) {
			super();
			this.eventSubTypePredicate = eventSubTypePredicate;
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			return eventSubTypePredicate.evaluate(evt.getEventSubType());
		}
		
	}
	
	/**
	 * Predicate for composing with condition with <pre>event.getEventClassName()</pre>  
	 */
	public static class WithEventClassNameRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final Predicate/*<String>*/ eventClassNamePredicate;
		
		public WithEventClassNameRecordEventSummaryPredicate(Predicate eventClassNamePredicate) {
			super();
			this.eventClassNamePredicate = eventClassNamePredicate;
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			return eventClassNamePredicate.evaluate(evt.getEventClassName());
		}
		
	}
	
	/**
	 * Predicate for composing with condition with <pre>event.getEventMethodName()</pre>  
	 */
	public static class WithEventMethodNameRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final Predicate/*<String>*/ eventMethodNamePredicate;
		
		public WithEventMethodNameRecordEventSummaryPredicate(Predicate eventMethodNamePredicate) {
			super();
			this.eventMethodNamePredicate = eventMethodNamePredicate;
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			return eventMethodNamePredicate.evaluate(evt.getEventMethodName());
		}
		
	}
	
	/**
	 * Predicate for composing with condition with <pre>event.getEventMethodDetail()</pre>  
	 */
	public static class WithEventMethodDetailRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final Predicate/*<String>*/ eventMethodDetailPredicate;
		
		public WithEventMethodDetailRecordEventSummaryPredicate(Predicate eventMethodDetailPredicate) {
			super();
			this.eventMethodDetailPredicate = eventMethodDetailPredicate;
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			return eventMethodDetailPredicate.evaluate(evt.getEventMethodDetail());
		}
		
	}

}
