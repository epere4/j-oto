package com.google.code.joto.eventrecorder.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.google.code.joto.eventrecorder.ui.capture.RecordEventsCapturePanel;
import com.google.code.joto.eventrecorder.ui.config.JotoConfigPanel;
import com.google.code.joto.eventrecorder.ui.conv.RecordEventsTableAndConvertersPanel;
import com.google.code.joto.eventrecorder.ui.table.AbstractRecordEventTableModel;

/**
 * Main UI facade for Joto
 */
public class JotoContextFacadePanel {

	protected JotoContext context;
	
	protected JPanel panel;
	
	protected JTabbedPane tabbedPane;

	protected JotoConfigPanel configPanel;

	protected RecordEventsCapturePanel capturePanel;
	
	protected AbstractRecordEventTableModel selectedRecordEventTableModel;
	
	protected RecordEventsTableAndConvertersPanel resultsConverterPanel;
	
	// ------------------------------------------------------------------------

	public JotoContextFacadePanel(JotoContext context) {
		this.context = context;
		initComponents();
	}

	private void initComponents() {
		panel = new JPanel();
		tabbedPane = new JTabbedPane();
		panel.add(tabbedPane);

		{ // tab : general / config
			configPanel = new JotoConfigPanel(context);
			tabbedPane.add("Config", configPanel.getJComponent());
		}

		{ // tab : capture (+ capture filter)
			capturePanel = new RecordEventsCapturePanel(context);
			tabbedPane.add("Capture", capturePanel.getJComponent());
		}

		{ // tab : aggregated display
			
		}

		{ // tab : selection table (+ display filter)
			// TODO selectedRecordEventTableModel
			
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
