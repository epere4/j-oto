package com.google.code.joto.ui.tree.aggrs;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.AbstractAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.ClassAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.MethodAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.RootPackageAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.TemplateMethodCallAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplatizer;
import com.google.code.joto.ui.tree.AggrRecordEventTreeModel;

/**
 *
 */
public class MethodCallEventTemplatizer implements AggrRecordEventTemplatizer {

	// ------------------------------------------------------------------------

	public MethodCallEventTemplatizer() {
	}

	// ------------------------------------------------------------------------

	
	@Override
	public boolean canHandle(AggrRecordEventTreeModel target, RecordEventSummary event) {
		return "methodCall".equals(event.getEventType());
	}

	@Override
	public AbstractAggrEventTreeNode aggregateTemplatizedEvent(AggrRecordEventTreeModel target, RecordEventSummary event) {
		String eventSubType = event.getEventSubType();
		String className = event.getEventClassName();
		String methodName = event.getEventMethodName();
		
		RootPackageAggrEventTreeNode rootPackageNode = target.getRootPackageNode();
		ClassAggrEventTreeNode classNode = rootPackageNode.getOrCreateRecursiveChildClass(className);	
		MethodAggrEventTreeNode methodNode = classNode.getOrCreateMethod(methodName);
		
		String templateCallKey = "dummy-params-templatekey"; // ... todo templatize params...
		
		TemplateMethodCallAggrEventTreeNode methodCallNode = methodNode.getOrCreateTemplateCall(templateCallKey);

		if ("request".equals(eventSubType)) {
			methodCallNode.addRequestEvent(event);	
		} else { // "response"
			methodCallNode.addResponseEvent(event);	
		}
		return methodCallNode;
	}
	
}
