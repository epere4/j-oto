package com.google.code.joto.eventrecorder.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.util.ui.IconUtils;
import com.google.code.joto.util.ui.JButtonUtils;

/**
 * swing Panel for controling Capture of RecordEvent in RecordEventStore
 * => for filtering + enable / disable ... 
 * 
 * contains configuration editor for capture filters (!= table view filter)
 * => may contains pluggeable configurations sub panels: for AOP settings, ...   
 *
 */
public class RecordEventsCapturePanel {

	protected JPanel panel;

	protected RecordEventStore eventStore;
	
	protected JToolBar toolbar;
	
	// ------------------------------------------------------------------------

	public RecordEventsCapturePanel() {
		initComponents();
	}

	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		
		toolbar = new JToolBar();
		panel.add(toolbar, BorderLayout.NORTH);
		
		ImageIcon playIcon = IconUtils.getBasic32().get("play");
		toolbar.add(JButtonUtils.snew(playIcon, "start record", this, "onButtonStartRecord"));

		ImageIcon pauseIcon = IconUtils.getBasic32().get("pause");
		toolbar.add(JButtonUtils.snew(pauseIcon, "pause record", this, "onButtonPauseRecord"));

		
		
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}
	
	public void setModel(RecordEventStore p) {
		this.eventStore = p;
	}
	
	public void onButtonStartRecord(ActionEvent event) {
		// TODO
	}

	public void onButtonPauseRecord(ActionEvent event) {
		// TODO
	}

}
