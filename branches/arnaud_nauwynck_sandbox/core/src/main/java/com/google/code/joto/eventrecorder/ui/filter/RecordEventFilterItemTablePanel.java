package com.google.code.joto.eventrecorder.ui.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.code.joto.util.ui.IconUtils;
import com.google.code.joto.util.ui.JButtonUtils;

/**
 * simple swing Table Panel containing RecordEventFilter table + detailed viewer/editor
 *
 */
public class RecordEventFilterItemTablePanel {

	private JPanel panel;

	private RecordEventFilterItemTableModel filterItemsTableModel;
	
	private JSplitPane tableDetailSplitPane;

	private JPanel filterItemsTablePane;
	private JToolBar filterItemsTableToolbar;
	private JScrollPane filterItemsTableScrollPane;
	private JTable filterItemsTable;
	
	private RecordEventFilterItemEditor detailedEditorPane;
	private PropertyChangeListener innerDetailedEditorPaneChangeListener; 

	private JButton newFilterButton;
	private JButton deleteFilterButton;
	
	
	// ------------------------------------------------------------------------

	public RecordEventFilterItemTablePanel(RecordEventFilterItemTableModel tableModel) {
		this.filterItemsTableModel = tableModel;
		initComponents();
	}

	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		tableDetailSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		panel.add(tableDetailSplitPane, BorderLayout.CENTER);
		
		filterItemsTablePane = new JPanel(new BorderLayout());
		filterItemsTable = new JTable(filterItemsTableModel);
		filterItemsTableScrollPane = new JScrollPane(filterItemsTable);
		filterItemsTablePane.add(filterItemsTableScrollPane, BorderLayout.CENTER);

		tableDetailSplitPane.setTopComponent(filterItemsTablePane);
		filterItemsTable.setPreferredScrollableViewportSize(new Dimension(500, 200));

		innerDetailedEditorPaneChangeListener =
			new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				RecordEventFilterItemTableModel tm = (RecordEventFilterItemTableModel) filterItemsTable.getModel();
				tm.fireTableDataChanged();
			}
		};
		filterItemsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) return;
				RecordEventFilterItem selectedRow = null;
				if (filterItemsTable.getSelectedRowCount() == 1) {
					int selectedRowIndex = filterItemsTable.getSelectedRow();
					RecordEventFilterItemTableModel tm = (RecordEventFilterItemTableModel) filterItemsTable.getModel();
					selectedRow = (selectedRowIndex != -1)? tm.getRow(selectedRowIndex) : null;
				}
				RecordEventFilterItem oldModel = detailedEditorPane.getModel();
				if (oldModel != null) {
					oldModel.removePropertyChangeSupport(innerDetailedEditorPaneChangeListener);
				}
				detailedEditorPane.setModel(selectedRow);
				if (selectedRow != null) {
					selectedRow.addPropertyChangeSupport(innerDetailedEditorPaneChangeListener);
				}
			}
		});
		
		detailedEditorPane = new RecordEventFilterItemEditor();
		tableDetailSplitPane.setBottomComponent(detailedEditorPane.getJComponent());
		
		{
			// toolbar buttons for Add / Remove .. in table
			filterItemsTableToolbar = new JToolBar();
			filterItemsTablePane.add(filterItemsTableToolbar, BorderLayout.SOUTH);

			newFilterButton = JButtonUtils.snew(IconUtils.eclipseGif.get("new"), "New", this, "onNewFilterButton");
			filterItemsTableToolbar.add(newFilterButton);
	
	//		addFilterButton = JButtonUtils.snew(IconUtils.eclipseGif.get("add"), "Add ...", this, "onAddFilterButton");
	//		filterItemsTableToolbar.add(newFilterButton);
	//		
	//		removeFilterButton = JButtonUtils.snew(IconUtils.eclipseGif.get("remove"), "Remove", this, "onRemoveFilterButton");
	//		filterItemsTableToolbar.add(removeFilterButton);
	
			deleteFilterButton = JButtonUtils.snew(IconUtils.eclipseGif.get("delete"), "Delete", this, "onDeleteFilterButton");
			filterItemsTableToolbar.add(deleteFilterButton);
		}
		
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}

	public void onNewFilterButton(ActionEvent event) {
		RecordEventFilterItem item = new RecordEventFilterItem();
		filterItemsTableModel.addRow(item);
	}

	public void onDeleteFilterButton(ActionEvent event) {
		List<RecordEventFilterItem> items = getSelectedItems();
		for(RecordEventFilterItem item : items) {
			filterItemsTableModel.removeRow(item);
		}
	}

	private List<RecordEventFilterItem> getSelectedItems() {
		List<RecordEventFilterItem> items = new ArrayList<RecordEventFilterItem>();
		int[] selectedViewRows = filterItemsTable.getSelectedRows();
		if (selectedViewRows != null && selectedViewRows.length != 0) {
			for(int viewRow : selectedViewRows) {
				int modelRow = viewRow; // no conversion model->view for sort yet?
				RecordEventFilterItem item = filterItemsTableModel.getRow(modelRow);
				if (item != null) {
					items.add(item);
				}
			}
		}
		return items;
	}

	public void onAddFilterButton(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onRemoveFilterButton(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

}
