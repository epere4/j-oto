package com.google.code.joto.eventrecorderui;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.code.joto.ObjectToCodeGenerator;
import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventHandle;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.thoughtworks.xstream.XStream;

/**
 * Swing panel for record/pause/continue buttons + show details ... 
 */
public class RecordEventPanel {

	private RecordEventStore eventStore;
	
	private JSplitPane splitPane;
	
	private RecordEventStoreTableModel recordEventTableModel;
	private JTable recordEventTable;
	private JTabbedPane selectedEventDetailsTabbedPane;
	private ScrolledTextPane xmlTextPaneTab;
	private ScrolledTextPane javaTextPaneTab;
	
	
	// -------------------------------------------------------------------------

	public RecordEventPanel(RecordEventStore eventStore) {
		this.eventStore = eventStore;
		
		this.recordEventTableModel = new RecordEventStoreTableModel(eventStore);
		this.recordEventTable = new JTable(recordEventTableModel);

		this.selectedEventDetailsTabbedPane = new JTabbedPane();

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				recordEventTable, selectedEventDetailsTabbedPane);
		splitPane.setDividerLocation(0.4);
		
		recordEventTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				onRecordEventSelectionChanged(e);
			}
		});

		xmlTextPaneTab = new ScrolledTextPane();
		javaTextPaneTab = new ScrolledTextPane();
		
		selectedEventDetailsTabbedPane.add("xml", xmlTextPaneTab.getJComponent());
		selectedEventDetailsTabbedPane.add("java", javaTextPaneTab.getJComponent());

	}

	//-------------------------------------------------------------------------

	public JComponent getJComponent() {
		return splitPane;
	}

	// -------------------------------------------------------------------------
	
	private void onRecordEventSelectionChanged(ListSelectionEvent e) {
		int firstIndex = e.getFirstIndex();
		int lastIndex = e.getLastIndex();
		System.out.println("ListSelectionEvent: " + firstIndex + " " + lastIndex + " adjusting:" + e.getValueIsAdjusting());
//		if (e.getValueIsAdjusting()) {
//			return; //??
//		}
		
		int[] selectedRows = recordEventTable.getSelectedRows();
		System.out.println("selected table lines: " + firstIndex + " " + lastIndex);
		
		List<RecordEventData> selectedEventDataList = new ArrayList<RecordEventData>();
		List<RecordEventHandle> events = eventStore.getEvents();
		if (events != null && events.size() != 0) {
			// int firstEventId = events.get(0).getEventId();
			for (int selectedRow : selectedRows) {
				RecordEventHandle eventHandle = events.get(selectedRow);
				RecordEventData eventData = eventStore.getEventData(eventHandle);
				selectedEventDataList.add(eventData);
			}
		}
		
		// display event data list in detailed tabbed pane
		String xmlText = eventsToXmlText(selectedEventDataList);
		xmlTextPaneTab.setText(xmlText);
		xmlTextPaneTab.scrollToStart();

		String javaText = eventsToJavaText(selectedEventDataList);
		javaTextPaneTab.setText(javaText);
		javaTextPaneTab.scrollToStart();

		
	}

	private String eventsToXmlText(List<RecordEventData> eventDataList) {
		StringWriter writer = new StringWriter();
		XStream xstream = new XStream();
		for(RecordEventData eventData : eventDataList) {
			RecordEventHandle eventHandle = eventData.getEventHandle();
			Object objectDataCopy = eventData.getObjectDataCopy();
			writer.append(eventHandle.getEventMethodName());
			writer.append("\n");

			xstream.toXML(objectDataCopy, writer);
			writer.append("\n");
			
			writer.append("\n");
		}
		return writer.getBuffer().toString();
	}

	private String eventsToJavaText(List<RecordEventData> eventDataList) {
		StringBuilder sb = new StringBuilder();
		ObjectToCodeGenerator objToCode = new ObjectToCodeGenerator();
		for(RecordEventData eventData : eventDataList) {
			RecordEventHandle eventHandle = eventData.getEventHandle();
			Object objectDataCopy = eventData.getObjectDataCopy();

			sb.append("meth:" + eventHandle.getEventMethodName() + "\n");
			sb.append("\n");
			sb.append("code: {\n\n");
			
			String stmtsStr = objToCode.objToStmtsString(objectDataCopy, "eventData");
			sb.append(stmtsStr);
			
			sb.append("\n} // code\n");
			
			sb.append("\n");
		}
		return sb.toString();
	}

}
	