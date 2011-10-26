package com.google.code.joto.eventrecorder.ui.capture;

import com.google.code.joto.eventrecorder.ui.JotoContext;
import com.google.code.joto.eventrecorder.ui.filter.RecordEventFilterFileTableModel;
import com.google.code.joto.eventrecorder.ui.filter.RecordEventFilterFileTablePanel;
import com.google.code.joto.util.ui.IconUtils;
import com.google.code.joto.util.ui.JButtonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Swing panel for record/pause/continue buttons + show details ... 
 */
public class EventRecorderToolbar {
	
	private static Logger log = LoggerFactory.getLogger(EventRecorderToolbar.class);
	
	private JotoContext context;
	
	private PropertyChangeListener modelChangeListener;
	
	private JToolBar toolbar;
	private JButton startRecordButton;
	private JButton stopRecordButton;
	private JButton showCaptureFiltersButton;
//	private JButton clearButton;

	private RecordEventFilterFileTablePanel captureFiltersPanel;
	private JFrame captureFiltersFrame;
	
	// -------------------------------------------------------------------------

	public EventRecorderToolbar(JotoContext context) {
		this.context = context;
		
		modelChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				onModelPropertyChange(evt);
			}
		};
		context.addPropertyChangeListener(modelChangeListener); 
		
		toolbar = new JToolBar();
		
		ImageIcon playIcon = IconUtils.getBasic32().get("play");
		startRecordButton = JButtonUtils.snew(playIcon, "start record", this, "onButtonStartRecord");
		toolbar.add(startRecordButton);

		ImageIcon pauseIcon = IconUtils.getBasic32().get("pause");
		stopRecordButton = JButtonUtils.snew(pauseIcon, "pause record", this, "onButtonPauseRecord");
		toolbar.add(stopRecordButton);

		
		ImageIcon filterIcon = IconUtils.getBasic32().get("filter");
		showCaptureFiltersButton = JButtonUtils.snew(filterIcon, "filter", this, "onButtonShowCaptureFilters");
		toolbar.add(showCaptureFiltersButton);
		RecordEventFilterFileTableModel captureFiltersTableModel = new RecordEventFilterFileTableModel();
		captureFiltersPanel = new RecordEventFilterFileTablePanel(captureFiltersTableModel);
	}

	public void dispose() {
		if (context != null && modelChangeListener != null) {
			context.removePropertyChangeListener(modelChangeListener);
		}
		modelChangeListener = null;
		context = null;
		if (captureFiltersFrame != null) {
			try {
				captureFiltersFrame.dispose();
			} catch(Exception ex) {
				log.warn("Failed to dispose ... ignore", ex);
			}
		}
	}
	
	//-------------------------------------------------------------------------

	public JComponent getJComponent() {
		return toolbar;
	}

	// -------------------------------------------------------------------------
	

	public void onButtonStartRecord(ActionEvent event) {
		context.startRecord();
	}

	public void onButtonPauseRecord(ActionEvent event) {
		context.stopRecord();
	}

	public void onButtonShowCaptureFilters(ActionEvent event) {
		if (captureFiltersFrame == null) {
			captureFiltersFrame = new JFrame();
			captureFiltersFrame.getContentPane().add(captureFiltersPanel.getJComponent());
			captureFiltersFrame.pack();
			
			captureFiltersFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//			captureFiltersFrame.addWindowListener(new WindowAdapter() {
//				@Override
//				public void windowClosing(WindowEvent e) {
//					super.windowClosing(e);
//				}
//				
//			});
			
			captureFiltersFrame.setVisible(true);
		} else {
			if (!captureFiltersFrame.isVisible()) {
				captureFiltersFrame.setVisible(true);
			}
		}
		captureFiltersFrame.requestFocus();
	}
	
	private void onModelPropertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (prop.equals(JotoContext.PROP_RECORDING_STATUS)) {
			stopRecordButton.setEnabled(context.isEnableStopRecord());
			startRecordButton.setEnabled(context.isEnableStartRecord());
		}
	}

}
	