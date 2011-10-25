package com.google.code.joto.eventrecorder.ui.config;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.google.code.joto.eventrecorder.ui.JotoContext;

/**
 * swing Panel for general information / Config about JotoContext
 *
 */
public class JotoContextPanel {

	protected JotoContext context;

	protected JPanel panel;

	protected JToolBar toolbar;
	
	// ------------------------------------------------------------------------

	public JotoContextPanel(JotoContext context) {
		this.context = context;
		initComponents();
	}

	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		
		toolbar = new JToolBar();
		panel.add(toolbar, BorderLayout.NORTH);
		
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}
	
	public void setContext(JotoContext p) {
		this.context = p;
	}
	

}
