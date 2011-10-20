package com.google.code.joto.util.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Helper class for using GridBagLayout, and building forms
 */
public class GridBagLayoutFormBuilder {
	
	protected JPanel parent;
	
	protected GridBagConstraints labelC;
	protected GridBagConstraints compC;
	
	// ------------------------------------------------------------------------
	
	public GridBagLayoutFormBuilder(JPanel parent) {
		this.parent = parent;
		this.labelC = new GridBagConstraints();
		this.compC = new GridBagConstraints();
		
		labelC.gridx = 0;
		labelC.gridy = 0;
		labelC.gridwidth = 1;
		labelC.gridheight = 1;
		labelC.anchor = GridBagConstraints.WEST;
		labelC.fill = GridBagConstraints.HORIZONTAL;
		labelC.insets = new Insets(5, 5, 5, 5);

		compC.gridx = 1;
		compC.gridy = 0;
		compC.gridwidth = 1;
		compC.gridheight = 1;
		compC.weightx = 1.0;
		compC.weighty = 0.0;
		compC.anchor = GridBagConstraints.WEST;
		compC.fill = GridBagConstraints.BOTH;
		compC.insets = new Insets(5, 5, 5, 5);

	}

	// ------------------------------------------------------------------------
	
	
	/**
	 * add 1 row in form, as "<<JLabel (LEFT)>>  <<Comp (FILL)>>"
	 * @param label
	 * @param comp
	 */
	public void addLabelComp(String label, JComponent comp) {
		parent.add(new JLabel(label), labelC);
		parent.add(comp, compC);
		newline();
	}
	
	public void addLabelRow(String label) {
		JLabel jlabel = new JLabel(label);
		parent.add(jlabel, labelC);
		newline();
	}

	public void addLabelCompFill2Rows(String label, JComponent jcomp) { 
		addLabelFillRow(label);
		addCompFillRow(jcomp);
	}

	public void addLabelFillRow(String label) {
		labelC.gridwidth = 2;

		JLabel jlabel = new JLabel(label);
		parent.add(jlabel, labelC);
		labelC.gridwidth = 1;
		newline();
	}

	public void addCompFillRow(JComponent jcomp) { 
		compC.gridx = 0; // tmp set
		compC.gridwidth = 2; 
		compC.fill = GridBagConstraints.BOTH;
		compC.weightx = 1.0;
		compC.weighty = 1.0;
		
		parent.add(jcomp, compC);
		
		// restore from tmp set
		compC.gridx = 1;
		compC.gridwidth = 1;
		
		newline();
	}
	

	private void newline() {
		labelC.gridy++;
		compC.gridy++;
	}
	
}
