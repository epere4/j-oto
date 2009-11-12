package com.google.code.joto.eventrecorderui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

/**
 * Swing panel for record/pause/continue buttons + show details ... 
 */
public class EventRecorderToolbar {

	private EventRecorderModel model;
	
	private PropertyChangeListener modelChangeListener;
	
	private JToolBar toolbar;
	private JButton recordButton;
	private JButton stopButton;
	private JButton clearButton;

	// -------------------------------------------------------------------------

	public EventRecorderToolbar(EventRecorderModel model) {
		this.model = model;
		
		modelChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				onModelPropertyChange(evt);
			}
		};
		model.addPropertyChangeListener(modelChangeListener); 
		
		toolbar = new JToolBar();
		recordButton = new JButton();
		recordButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRecordButton(); 
			}
		});
		stopButton = new JButton();
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onStopButton(); 
			}
		});
		clearButton = new JButton();
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClearButton(); 
			}
		});

		toolbar.add(recordButton);
		toolbar.add(stopButton);
		toolbar.add(clearButton);

	}

	//-------------------------------------------------------------------------

	public JComponent getThisJComponent() {
		return toolbar;
	}

	// -------------------------------------------------------------------------
	
	private void onRecordButton() {
		model.startRecord();
	}
	
	private void onStopButton() {
		model.stopRecord();
	}

	private void onClearButton() {
		
		
	}

	private void onModelPropertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (prop.equals(EventRecorderModel.PROP_RECORDING_STATUS)) {
			stopButton.setEnabled(model.isEnableStopRecord());
			recordButton.setEnabled(model.isEnableStartRecord());
		}
	}

}
	