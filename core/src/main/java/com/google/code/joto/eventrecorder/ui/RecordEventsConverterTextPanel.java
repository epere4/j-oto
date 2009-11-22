package com.google.code.joto.eventrecorder.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.swing.JComponent;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;

/**
 * a TextPanel (with ScrolledPane and misc toolbars), 
 * for displaying list of RecordEvent as text
 *
 */
public class RecordEventsConverterTextPanel {

	private RecordEventsProcessorFactory<PrintStream> converterFactory;

	private ScrolledTextPane textPane;

	//-------------------------------------------------------------------------

	public RecordEventsConverterTextPanel(RecordEventsProcessorFactory<PrintStream> converterFactory) {
		this.converterFactory = converterFactory;
		textPane = new ScrolledTextPane();
	}

	//-------------------------------------------------------------------------

	public JComponent getJComponent() {
		return textPane.getJComponent();
	}
	
	public void setRecordEventDataList(List<RecordEventData> eventDataList) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(buffer);
		try {
			RecordEventsProcessor converter = converterFactory.create(out);
			for(RecordEventData eventData : eventDataList) {
				RecordEventSummary event = eventData.getEventSummary();
				Object eventObjectData = eventData.getObjectData();
				converter.processEvent(event, eventObjectData);
			}
		} catch(Exception ex) {
			out.println();
			out.print("Failed to convert RecordEvent(s) to text!\n");
			ex.printStackTrace(out);
		}
		out.flush();
		String textResult = buffer.toString();
		textPane.setText(textResult);
	}
	
}
