package com.google.code.joto.eventrecorder.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.google.code.joto.eventrecorder.ui.capture.RecordEventsCapturePanel;
import com.google.code.joto.eventrecorder.ui.config.JotoContextPanel;
import com.google.code.joto.eventrecorder.ui.conv.RecordEventsTableAndConvertersPanel;

/**
 * Main UI facade for Joto
 */
public class JotoContextFacadePanel {

	protected JotoContext context;
	
	protected JPanel panel;
	
	protected JTabbedPane tabbedPane;

	protected JotoContextPanel contextPanel;

	protected RecordEventsCapturePanel capturePanel;
	
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
			contextPanel = new JotoContextPanel(context);
			tabbedPane.add("Main", contextPanel.getJComponent());
		}

		{ // tab : capture
			capturePanel = new RecordEventsCapturePanel(context);
			tabbedPane.add("Capture", capturePanel.getJComponent());
		}

		{ // tab : aggregated display
			
		}

		{ // tab : display filter
			
		}

		{ // tab : selection table
			
		}

		{ // tab : result converters
			resultsConverterPanel = new RecordEventsTableAndConvertersPanel(context);
			tabbedPane.add("Results", resultsConverterPanel.getJComponent());
		}
		
		
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}
	
	
}
