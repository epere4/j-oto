package com.google.code.joto.ui.tree;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTree;

/**
 * a Tree View for aggregating similar (="templatized") RecordEvent together
 */
public class AggrRecordEventTreeView {

	private JPanel panel;
	
	private AggrRecordEventTreeModel treeModel;
	private JTree jtree;
	
	// ------------------------------------------------------------------------

	public AggrRecordEventTreeView(AggrRecordEventTreeModel treeModel) {
		this.treeModel = treeModel;
		initComponents();
	}

	private void initComponents() {
		this.panel = new JPanel(new BorderLayout());
		
		jtree = new JTree(treeModel);
		panel.add(jtree, BorderLayout.CENTER);
	}
	
	// ------------------------------------------------------------------------
	
	public JComponent getJComponent() {
		return panel;
	}
	
}
