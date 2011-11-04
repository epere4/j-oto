package com.google.code.joto.ui.capture;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.filter.RecordEventFilterCategoryModel;
import com.google.code.joto.ui.filter.RecordEventFilterFileTablePanel;
import com.google.code.joto.util.ui.GridBagLayoutFormBuilder;
import com.google.code.joto.util.ui.JButtonUtils;
import com.google.code.joto.util.ui.JCheckBoxUtils;

/**
 * abstract base-class for Category Capture Panel
 * <p/>
 * typical sub-classes: MethCall Capture, AWT-Event capture, Log,  ... 
 */
public abstract class RecordEventsCaptureCategoryPanel {

	protected JotoContext context;
	protected final String categoryName;
	protected RecordEventFilterCategoryModel filterCategoryModel;

	private JPanel panel;

	protected JCheckBox filterEnableEventsCheckBox;

	private JCheckBox showEmbeddedFilterTablePanelCheckBox;
	private RecordEventFilterFileTablePanel filtersPanel;

	private JButton openExternalFilterTableFrame;

	// ------------------------------------------------------------------------
	
	public RecordEventsCaptureCategoryPanel(JotoContext context, String categoryName) {
		this.context = context;
		this.categoryName = categoryName;
		this.filterCategoryModel = context.getOrCreateFilterCategoryModel(categoryName);
		initComponents();
	}

	public String getCategoryName() {
		return categoryName;
	}
	
	private void initComponents() {
		this.panel = new JPanel(new GridBagLayout());
		GridBagLayoutFormBuilder b = new GridBagLayoutFormBuilder(panel);
		
		filterEnableEventsCheckBox = JCheckBoxUtils.snew("Enable Filter Events", true, this, "onCheckboxFilterEnableEvents");
		b.addCompFillRow(filterEnableEventsCheckBox);

		{
			JPanel showFiltersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
			showEmbeddedFilterTablePanelCheckBox = JCheckBoxUtils.snew("show embedded filters table", false, this, "onCheckboxShowEmbeddedFilterTablePanel");
			showFiltersPanel.add(showEmbeddedFilterTablePanelCheckBox);
			
			openExternalFilterTableFrame = JButtonUtils.snew("open filters table view", this, "onButtonOpenExternalFilterTableFrame");
			showFiltersPanel.add(openExternalFilterTableFrame);
			b.addCompFillRow(showFiltersPanel);
		}
		
		filtersPanel = new RecordEventFilterFileTablePanel(filterCategoryModel.getFilterItemTableModel());
		b.addCompFillRow(filtersPanel.getJComponent());
				
		filtersPanel.getJComponent().setVisible(showEmbeddedFilterTablePanelCheckBox.isSelected());
		postInitComponents(b);
	}

	protected void postInitComponents(GridBagLayoutFormBuilder b) {
		// cf sub-classes
	}

	// ------------------------------------------------------------------------
	

	public JComponent getJComponent() {
		return panel;
	}

	public String getTabName() {
		return filterCategoryModel.getName();
	}
	
	/** called by introspection, GUI callback for JCheckBox showEmbeddedFilterTablePanelCheckBox */
	public void onCheckboxFilterEnableEvents(ActionEvent event) {
		filterCategoryModel.getResultFilteringEventWriter().setEnable(filterEnableEventsCheckBox.isSelected());
	}
	
	/** called by introspection, GUI callback for JCheckBox showEmbeddedFilterTablePanelCheckBox */
	public void onCheckboxShowEmbeddedFilterTablePanel(ActionEvent event) {
		filtersPanel.getJComponent().setVisible(showEmbeddedFilterTablePanelCheckBox.isSelected());
	}

	/** called by introspection, GUI callback for JCheckBox showEmbeddedFilterTablePanelCheckBox */
	public void onButtonOpenExternalFilterTableFrame(ActionEvent event) {
		filterCategoryModel.showFilterFrame();
	}
	
	
}
