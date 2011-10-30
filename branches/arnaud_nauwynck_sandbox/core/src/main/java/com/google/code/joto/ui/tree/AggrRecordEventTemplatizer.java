package com.google.code.joto.ui.tree;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.AbstractAggrEventTreeNode;

public interface AggrRecordEventTemplatizer {

	public AbstractAggrEventTreeNode aggregateTemplatizedEvent(
			AggrRecordEventTreeModel target,
			RecordEventData event);
	
}
