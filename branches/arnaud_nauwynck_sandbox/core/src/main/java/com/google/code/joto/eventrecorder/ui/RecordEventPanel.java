package com.google.code.joto.eventrecorder.ui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.code.joto.ObjectToCodeGenerator;
import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.calls.ObjectReplacementMap;
import com.google.code.joto.eventrecorder.processor.DispatcherRecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;
import com.google.code.joto.eventrecorder.processor.impl.MethodCallToCodeRecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.impl.ObjToCodeRecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.impl.XStreamFormatterRecordEventsProcessor;
import com.thoughtworks.xstream.XStream;

/**
 * Swing panel for record/pause/continue buttons + show details ... 
 */
public class RecordEventPanel {

	private RecordEventStore eventStore;
	
	private JSplitPane splitPane;
	
	private RecordEventStoreTableModel recordEventTableModel;
	private JTable recordEventTable;
	
	/**
	 * contains child component for displaying selected RecordEvent as Text.
	 * predefined tabs:
	 * <ul>
	 * <li>XStream dump (RecordEvent ObjectData -> Xml)</li>
	 * <li>Joto Reverse for Java code construction (RecordEvent ObjectData -> Java "new/call" code)</li>
	 */
	private JTabbedPane selectionTabbedPane;

	/** downcast helper... currently redundant with selectionTabbedPane! */
	private List<RecordEventsConverterTextPanel> selectedEventConverterTextPanes = 
		new ArrayList<RecordEventsConverterTextPanel>();
	
	private ObjectReplacementMap objectReplacementMap;
	
	// -------------------------------------------------------------------------

	public RecordEventPanel(RecordEventStore eventStore) {
		this.eventStore = eventStore;
		
		this.recordEventTableModel = new RecordEventStoreTableModel(eventStore);
		this.recordEventTable = new JTable(recordEventTableModel);

		this.selectionTabbedPane = new JTabbedPane();

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				recordEventTable, selectionTabbedPane);
		splitPane.setDividerLocation(0.4);
		
		recordEventTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				onRecordEventSelectionChanged(e);
			}
		});

		// predefined text converters:
		{// Xml XStream text converter
			RecordEventsProcessorFactory<PrintStream> eventConverterFactory =
				new XStreamFormatterRecordEventsProcessor.Factory(new XStream());
			RecordEventsConverterTextPanel comp = 
				new RecordEventsConverterTextPanel(eventConverterFactory);
			selectionTabbedPane.add("xml", comp.getJComponent());
			selectedEventConverterTextPanes.add(comp);
		}
		
		{// Reverse Java "new/call" text converter
			ObjectToCodeGenerator objToCode = new ObjectToCodeGenerator();
			
			RecordEventsProcessorFactory<PrintStream> objConverterFactory =
				new ObjToCodeRecordEventsProcessor.Factory(objToCode);
			
			RecordEventsProcessorFactory<PrintStream> methCallConverterFactory =
				new MethodCallToCodeRecordEventsProcessor.Factory(
					objToCode, objectReplacementMap);
			
			Map<String,RecordEventsProcessorFactory<PrintStream>> eventTypeToFactory =
				new HashMap<String,RecordEventsProcessorFactory<PrintStream>>();
			eventTypeToFactory.put("testObj", objConverterFactory);
			eventTypeToFactory.put("methCall", methCallConverterFactory);
			
			RecordEventsProcessorFactory<PrintStream> dispatcherConverterFactory =
				new DispatcherRecordEventsProcessor.Factory<PrintStream>(
						eventTypeToFactory, objConverterFactory);
			
			RecordEventsConverterTextPanel comp = 
				new RecordEventsConverterTextPanel(dispatcherConverterFactory);
			selectionTabbedPane.add("java", comp.getJComponent());
			selectedEventConverterTextPanes.add(comp);
		}

	}

	//-------------------------------------------------------------------------

	public JComponent getJComponent() {
		return splitPane;
	}

	// -------------------------------------------------------------------------
	
	private void onRecordEventSelectionChanged(ListSelectionEvent e) {
		int[] selectedRows = recordEventTable.getSelectedRows();
		
		List<RecordEventData> selectedEventDataList = new ArrayList<RecordEventData>();
		for (int selectedRow : selectedRows) {
			RecordEventSummary eventHandle = recordEventTableModel.getEventRow(selectedRow);
			RecordEventData eventData = eventStore.getEventData(eventHandle);
			selectedEventDataList.add(eventData);
		}
		
		// display event data list in detailed tabbed pane
		for(RecordEventsConverterTextPanel comp : selectedEventConverterTextPanes) {
			comp.setRecordEventDataList(selectedEventDataList);
		}
	
	}

}
	