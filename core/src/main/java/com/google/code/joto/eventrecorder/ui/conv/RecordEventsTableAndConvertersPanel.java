package com.google.code.joto.eventrecorder.ui.conv;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import com.google.code.joto.ObjectToCodeGenerator;
import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.ext.calls.MethodCallToCodeRecordEventsProcessor;
import com.google.code.joto.eventrecorder.ext.calls.ObjectReplacementMap;
import com.google.code.joto.eventrecorder.ext.log.Log4jToCodeRecordEventsProcessor;
import com.google.code.joto.eventrecorder.ext.log.LogbackToCodeRecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.DispatcherRecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;
import com.google.code.joto.eventrecorder.processor.impl.ObjToCodeRecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.impl.XStreamFormatterRecordEventsProcessor;
import com.google.code.joto.eventrecorder.ui.JotoContext;
import com.google.code.joto.eventrecorder.ui.table.RecordEventStoreTableModel;
import com.thoughtworks.xstream.XStream;

/**
 * Swing panel for selecting RecordEvent in table, 
 * and show pluggeable representations as Xml / JavaCode / JUnit / ... 
 */
public class RecordEventsTableAndConvertersPanel {

	private JotoContext context;
	
	private JSplitPane splitPane;
	
	private RecordEventStoreTableModel recordEventTableModel;
	private JScrollPane recordEventScrollPane;
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

	public RecordEventsTableAndConvertersPanel(JotoContext context) {
		this.context = context;
		
		this.recordEventTableModel = new RecordEventStoreTableModel(context.getEventStore());
		
		this.recordEventTable = new JTable(recordEventTableModel);
		recordEventTable.setRowSorter(new TableRowSorter<RecordEventStoreTableModel>(recordEventTableModel));
		
		this.recordEventScrollPane = new JScrollPane(recordEventTable);
		
		this.selectionTabbedPane = new JTabbedPane();

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				recordEventScrollPane, selectionTabbedPane);
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
	
			RecordEventsProcessorFactory<PrintStream> logbackToCommentConverterFactory =
				new LogbackToCodeRecordEventsProcessor.Factory(true);

			RecordEventsProcessorFactory<PrintStream> log4jToCommentConverterFactory =
				new Log4jToCodeRecordEventsProcessor.Factory(true);

			Map<String,RecordEventsProcessorFactory<PrintStream>> eventTypeToFactory =
				new HashMap<String,RecordEventsProcessorFactory<PrintStream>>();
			eventTypeToFactory.put("testObj", objConverterFactory);
			eventTypeToFactory.put("methCall", methCallConverterFactory);
			eventTypeToFactory.put("logback", logbackToCommentConverterFactory);
			eventTypeToFactory.put("log4j", log4jToCommentConverterFactory);
			
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
			RecordEventData eventData = context.getEventStore().getEventData(eventHandle);
			selectedEventDataList.add(eventData);
		}
		
		// display event data list in detailed tabbed pane
		for(RecordEventsConverterTextPanel comp : selectedEventConverterTextPanes) {
			comp.setRecordEventDataList(selectedEventDataList);
		}
	
	}

}
