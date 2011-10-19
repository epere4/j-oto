package com.google.code.joto.eventrecorder.ui.filter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.google.code.joto.eventrecorder.predicate.RecortEventSummaryPredicateUtils;
import com.google.code.joto.eventrecorder.ui.ScrolledTextPane;
import com.thoughtworks.xstream.XStream;

/**
 * simple swing View/Editor for RecordEventFilter model
 */
public class RecordEventFilterEditor {

	private JPanel panel;
	
	private RecordEventFilter model;
	
	private JTextField nameField;
	private JTextField descriptionField;

	private JTextField persistentFileField;

	private JTextField eventIdPredicateDescription;
	private JTextField eventDatePredicateDescription;
	private JTextField threadNamePredicateDescription;
	private JTextField eventTypePredicateDescription;
	private JTextField eventSubTypePredicateDescription;
	private JTextField eventClassNamePredicateDescription;
	private JTextField eventMethodNamePredicateDescription;
	private JTextField eventMethodDetailPredicateDescription;
	private JTextField correlatedEventIdPredicateDescription;

	private ScrolledTextPane detailedXmlPredicateTextPane;
	private JToolBar detailedViewerToolbar;
	
	private XStream predicateXStream = RecortEventSummaryPredicateUtils.createDefaultPredicateXStream();
	
	// ------------------------------------------------------------------------

	public RecordEventFilterEditor() {
		initComponents();
	}


	private void initComponents() {
		panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints labelC = new GridBagConstraints();
		labelC.gridx = 0;
		labelC.gridy = 0;
		labelC.gridwidth = 1;
		labelC.gridheight = 1;
		labelC.anchor = GridBagConstraints.WEST;
		labelC.fill = GridBagConstraints.HORIZONTAL;
		labelC.insets = new Insets(2, 2, 2, 2); // (5, 5, 5, 5);

		GridBagConstraints compC = new GridBagConstraints();
		compC.gridx = 1;
		compC.gridy = 0;
		compC.gridwidth = 1;
		compC.gridheight = 1;
		compC.weightx = 1.0;
		compC.weighty = 0.0;
		compC.anchor = GridBagConstraints.WEST;
		compC.fill = GridBagConstraints.HORIZONTAL;
		// compC.insets = new Insets(2, 2, 2, 2); // (5, 5, 5, 5);

		
		nameField = new JTextField();
		addLabelComp(panel, labelC, compC, "Name", nameField);
		descriptionField = new JTextField();
		addLabelComp(panel, labelC, compC, "Description", descriptionField);

		persistentFileField = new JTextField();
		addLabelComp(panel, labelC, compC, "File", persistentFileField);

		eventIdPredicateDescription = new JTextField();
		addLabelComp(panel, labelC, compC, "Id ~~", eventIdPredicateDescription);
		eventDatePredicateDescription = new JTextField();
		addLabelComp(panel, labelC, compC, "Date ~~", eventDatePredicateDescription);
		threadNamePredicateDescription = new JTextField();
		addLabelComp(panel, labelC, compC, "ThreadName ~~", threadNamePredicateDescription);
		eventTypePredicateDescription = new JTextField();

		addLabelComp(panel, labelC, compC, "eventType ~~", eventTypePredicateDescription);
		eventSubTypePredicateDescription = new JTextField();
		addLabelComp(panel, labelC, compC, "eventSubType ~~", eventSubTypePredicateDescription);
		eventClassNamePredicateDescription = new JTextField();
		addLabelComp(panel, labelC, compC, "eventClassName ~~", eventClassNamePredicateDescription);
		eventMethodNamePredicateDescription = new JTextField();
		addLabelComp(panel, labelC, compC, "eventMethodName ~~", eventMethodNamePredicateDescription);
		eventMethodDetailPredicateDescription = new JTextField();
		addLabelComp(panel, labelC, compC, "eventMethodDetail ~~", eventMethodDetailPredicateDescription);

		correlatedEventIdPredicateDescription = new JTextField();
		addLabelComp(panel, labelC, compC, "correlatedEventId ~~", correlatedEventIdPredicateDescription);
		

		JLabel xmlLabel = new JLabel("xml");
		panel.add(xmlLabel, labelC);
		labelC.gridy++;
		compC.gridy++;
		
		compC.gridx = 0;
		compC.gridwidth = 2;
		compC.fill = GridBagConstraints.BOTH;
		compC.weightx = 1.0;
		compC.weighty = 1.0;
		detailedXmlPredicateTextPane = new ScrolledTextPane();
		panel.add(detailedXmlPredicateTextPane.getJComponent(), compC);
		
		detailedViewerToolbar = detailedXmlPredicateTextPane.getToolbar();
		
	}

	private static void addLabelComp(JPanel panel, GridBagConstraints labelC, GridBagConstraints compC, String label, JComponent comp) {
		panel.add(new JLabel(label), labelC);
		panel.add(comp, compC);
		labelC.gridy++;
		compC.gridy++;
	}
	
	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}

	public JToolBar getDetailedViewerToolbar() {
		return detailedViewerToolbar;
	}

	public void setModel(RecordEventFilter p) {
		if (model != null) {
//			model.removePropertyChangeListener(innerPropChangeListener);
		}
		this.model = p;
		if (model != null) {
//			model.addPropertyChangeListener(innerPropChangeListener);
			updateModel2View();
		}		
	}

	public void updateModel2View() {
		// use tmp new empty instead when null
		RecordEventFilter m = (model != null)? model : new RecordEventFilter(); 
		
		nameField.setText(m.getName());
		descriptionField.setText(m.getDescription());

		File persistentFile = m.getPersistentFile();
		persistentFileField.setText((persistentFile != null)? persistentFile.getAbsolutePath() : "");

		eventIdPredicateDescription.setText(m.getEventIdPredicateDescription());
		eventDatePredicateDescription.setText(m.getEventDatePredicateDescription());
		threadNamePredicateDescription.setText(m.getThreadNamePredicateDescription());
		eventTypePredicateDescription.setText(m.getEventTypePredicateDescription());
		eventSubTypePredicateDescription.setText(m.getEventSubTypePredicateDescription());
		eventClassNamePredicateDescription.setText(m.getEventClassNamePredicateDescription());
		eventMethodNamePredicateDescription.setText(m.getEventMethodNamePredicateDescription());
		eventMethodDetailPredicateDescription.setText(m.getEventMethodDetailPredicateDescription());
		correlatedEventIdPredicateDescription.setText(m.getCorrelatedEventIdPredicateDescription());

		String xstreamPredicateXml = predicateXStream.toXML(m.getEventPredicate());
		detailedXmlPredicateTextPane.setText(xstreamPredicateXml);
	}
	
	public void updateView2Model(RecordEventFilter m) {
		m.setName(nameField.getText());
		m.setDescription(descriptionField.getText());

		File persistentFile = new File(persistentFileField.getText());
		m.setPersistentFile(persistentFile);

		m.setEventIdPredicateDescription(eventIdPredicateDescription.getText());
		m.setEventDatePredicateDescription(eventDatePredicateDescription.getText());
		m.setThreadNamePredicateDescription(threadNamePredicateDescription.getText());
		m.setEventTypePredicateDescription(eventTypePredicateDescription.getText());
		m.setEventSubTypePredicateDescription(eventSubTypePredicateDescription.getText());
		m.setEventClassNamePredicateDescription(eventClassNamePredicateDescription.getText());
		m.setEventMethodNamePredicateDescription(eventMethodNamePredicateDescription.getText());
		m.setEventMethodDetailPredicateDescription(eventMethodDetailPredicateDescription.getText());
		m.setCorrelatedEventIdPredicateDescription(correlatedEventIdPredicateDescription.getText());

		String xstreamPredicateXml = predicateXStream.toXML(m.getEventPredicate());
		detailedXmlPredicateTextPane.setText(xstreamPredicateXml);
		
	}
	
}
