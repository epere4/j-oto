package com.google.code.joto.eventrecorder.ui.filter;

import com.google.code.joto.eventrecorder.predicate.RecortEventSummaryPredicateUtils;
import com.thoughtworks.xstream.XStream;

/**
 *
 */
public class RecordEventFilterItemUtils {

	public static XStream getXStream() {
		XStream xstream = new XStream();
		registerDefaultXStreamAlias(xstream);
		return xstream;
	}
	
	public static void registerDefaultXStreamAlias(XStream res) {
		res.alias("eventFilter", RecordEventFilterItem.class);
		RecortEventSummaryPredicateUtils.registerDefaultPredicateXStreamAlias(res);
	}
	
}
