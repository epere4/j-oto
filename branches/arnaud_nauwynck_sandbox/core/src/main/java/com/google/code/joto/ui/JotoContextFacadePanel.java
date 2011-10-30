package com.google.code.joto.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.google.code.joto.ui.capture.RecordEventsCapturePanel;
import com.google.code.joto.ui.config.JotoConfigPanel;
import com.google.code.joto.ui.conv.RecordEventsTableAndConvertersPanel;
import com.google.code.joto.ui.table.AbstractRecordEventTableModel;
import com.google.code.joto.ui.table.RecordEventStoreTableModel;
import com.google.code.joto.ui.tree.AggrRecordEventTreeModel;
import com.google.code.joto.ui.tree.AggrRecordEventTreeView;

/**
 * Main UI facade for Joto
 */
public class JotoContextFacadePanel {

	protected JotoContext context;
	
	protected JPanel panel;
	
	protected JTabbedPane tabbedPane;

	protected JotoConfigPanel configPanel;

	protected RecordEventsCapturePanel capturePanel;
	
	protected AggrRecordEventTreeView aggrTreeView;
	
	protected AbstractRecordEventTableModel selectedRecordEventTableModel;
	
	protected RecordEventsTableAndConvertersPanel resultsConverterPanel;
	
	// ------------------------------------------------------------------------

	public JotoContextFacadePanel(JotoContext context) {
		this.context = context;
		initComponents();
	}

	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		
		tabbedPane = new JTabbedPane();
		panel.add(tabbedPane, BorderLayout.CENTER);

		{ // tab : general / config
			configPanel = new JotoConfigPanel(context);
			tabbedPane.add("Config", configPanel.getJComponent());
		}

		{ // tab : capture (+ capture filter)
			capturePanel = new RecordEventsCapturePanel(context);
			tabbedPane.add("Capture", capturePanel.getJComponent());
		}

		{ // tab : aggregated display
			AggrRecordEventTreeModel aggrTreeModel = new AggrRecordEventTreeModel(context);
			aggrTreeView = new AggrRecordEventTreeView(aggrTreeModel);
			tabbedPane.add("Aggr Tree", aggrTreeView.getJComponent());
		}

		{ // tab : selection table (+ display filter)
			// TODO selectedRecordEventTableModel
			if (selectedRecordEventTableModel == null) {
			    selectedRecordEventTableModel = new RecordEventStoreTableModel(context.getEventStore());
			}
		}

		{ // tab : result converters
			resultsConverterPanel = new RecordEventsTableAndConvertersPanel(context, selectedRecordEventTableModel);
			tabbedPane.add("Results", resultsConverterPanel.getJComponent());
		}
		
		
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}
	
	
}
