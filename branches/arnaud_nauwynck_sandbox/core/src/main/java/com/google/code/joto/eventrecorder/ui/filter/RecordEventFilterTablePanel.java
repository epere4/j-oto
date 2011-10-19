package com.google.code.joto.eventrecorder.ui.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * simple swing Table Panel containing RecordEventFilter table + detailed viewer/editor
 *
 */
public class RecordEventFilterTablePanel {

	private JPanel panel;

	private JSplitPane splitPane;

//	private JToolBar tableToolbar;
	private JScrollPane tableScrollPane;
	private JTable tablePane;
	
	private RecordEventFilterEditor detailedEditorPane;

	private JButton saveFilterButton;
	private JButton newFilterButton;
	private JButton deleteFilterButton;
	
	// ------------------------------------------------------------------------

	public RecordEventFilterTablePanel(RecordEventFilterTableModel tableModel) {
		initComponents(tableModel);
	}

	private void initComponents(RecordEventFilterTableModel tableModel) {
		panel = new JPanel(new BorderLayout());
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		panel.add(splitPane, BorderLayout.CENTER);
//		tableToolbar = new JToolBar();
//		panel.add(tableToolbar, BorderLayout.NORTH);
		
		tablePane = new JTable(tableModel);
		tableScrollPane = new JScrollPane(tablePane);
		splitPane.setTopComponent(tableScrollPane);
		tablePane.setPreferredScrollableViewportSize(new Dimension(500, 200));
		
		tablePane.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int rowIndex = e.getFirstIndex();
				RecordEventFilterTableModel tm = (RecordEventFilterTableModel) tablePane.getModel();
				RecordEventFilter row = (rowIndex != -1)? tm.getRow(rowIndex) : null;
				detailedEditorPane.setModel(row);
			}
		});
		
		detailedEditorPane = new RecordEventFilterEditor();
		splitPane.setBottomComponent(detailedEditorPane.getJComponent());
		
		// toolbar buttons for Add / Remove .. in table
		JToolBar detailedViewerToolbar = detailedEditorPane.getDetailedViewerToolbar(); // reuse same toolbar?
		saveFilterButton = new JButton();
		saveFilterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSaveFilterButton();
			}
		});
		detailedViewerToolbar.add(saveFilterButton);

		newFilterButton = new JButton();
		newFilterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onNewFilterButton();
			}
		});
		detailedViewerToolbar.add(newFilterButton);
		
		deleteFilterButton = new JButton();
		deleteFilterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onDeleteFilterButton();
			}
		});
		detailedViewerToolbar.add(deleteFilterButton);
		
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}

	private void onSaveFilterButton() {
		// TODO Auto-generated method stub
		
	}

	private void onNewFilterButton() {
		// TODO Auto-generated method stub
		
	}

	private void onDeleteFilterButton() {
		// TODO Auto-generated method stub
		
	}

}
