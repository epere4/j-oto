package com.google.code.joto.ui.tree;

import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.collections.Predicate;

import com.google.code.joto.eventrecorder.DefaultRecordEventChangeVisitor;
import com.google.code.joto.eventrecorder.DefaultVisitorRecordEventListener;
import com.google.code.joto.eventrecorder.RecordEventChangeVisitor;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.AbstractAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.PackageAggrEventTreeNode;

/**
 * a swing TreeModel adapter for aggregating RecordEventTree per template category
 *
 */
public class AggrRecordEventTreeModel extends DefaultTreeModel {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	private JotoContext context;
	
	// ... implicit from super: private DefaultRecordEventTemplateTreeNode rootNode;

	private RecordEventChangeVisitor innerRecordEventChangeListener = new InnerRecordEventChangeListener();

	// pluggable dispatcher using event Predicate + RecordEventTemplatizer
	private class RecordEventTemplatizerEntry {
		Predicate predicate;
		AggrRecordEventTemplatizer templatizer;
	}
	// private Map<String,List<RecordEventTemplatizerEntry>> 
	
	// ------------------------------------------------------------------------
	
	public AggrRecordEventTreeModel(JotoContext context) {
		super(new PackageAggrEventTreeNode(null, ""));
		this.context = context;

		context.getEventStore().getEventsAndAddEventListener(0, 
				new DefaultVisitorRecordEventListener(innerRecordEventChangeListener));

	}

	// ------------------------------------------------------------------------

	public JotoContext getContext() {
		return context;
	}
	
	public AbstractAggrEventTreeNode getRoot2() {
		return (AbstractAggrEventTreeNode) super.getRoot();
	}

	// ------------------------------------------------------------------------

	private class InnerRecordEventChangeListener extends DefaultRecordEventChangeVisitor {

		@Override
		public void caseAddEvent(AddRecordEventStoreEvent event) {
			// TODO Auto-generated method stub
		}
	};
}
