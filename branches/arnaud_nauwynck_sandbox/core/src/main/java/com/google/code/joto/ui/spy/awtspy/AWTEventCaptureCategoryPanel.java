package com.google.code.joto.ui.spy.awtspy;

import java.awt.AWTEvent;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.spy.awtspy.AWTRecordEventWriterSpy;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.capture.RecordEventsCaptureCategoryPanel;
import com.google.code.joto.util.ui.GridBagLayoutFormBuilder;
import com.google.code.joto.util.ui.JButtonUtils;
import com.google.code.joto.util.ui.JCheckBoxUtils;

/**
 *
 */
public class AWTEventCaptureCategoryPanel extends RecordEventsCaptureCategoryPanel {

	public static final String AWTSPY_CAPTURE_CATEGORY = "AWTSpy";
	
	private static Logger log = LoggerFactory.getLogger(AWTEventCaptureCategoryPanel.class);
	
	private AWTRecordEventWriterSpy awtSpy;

	private JCheckBox activateAWTToolkitListenerCheckBox;

	private JCheckBox componentEventMaskCheckBox;
	private JCheckBox containerEventMaskCheckBox;
	private JCheckBox focusEventMaskCheckBox;
	private JCheckBox keyEventMaskCheckBox;
	private JCheckBox mouseEventMaskCheckBox;
	private JCheckBox mouseMotionEventMaskCheckBox;
	private JCheckBox windowEventMaskCheckBox;
	private JCheckBox actionEventMaskCheckBox;
	private JCheckBox adjustmentEventMaskCheckBox;
	private JCheckBox itemEventMaskCheckBox;
	private JCheckBox textEventMaskCheckBox;
	private JCheckBox inputMethodEventMaskCheckBox;
	private JCheckBox paintEventMaskCheckBox; 
	private JCheckBox invocationEventMaskCheckBox;
	private JCheckBox hierarchyEventMaskCheckBox;
	private JCheckBox hierarchyBoundsEventMaskCheckBox;
	private JCheckBox mouseWheelEventMaskCheckBox;
	private JCheckBox windowStateEventMaskCheckBox;
	private JCheckBox windowFocusEventMaskCheckBox;
	
	private JCheckBox[] eventMaskCheckBoxes;
	
	

//	protected boolean enableComponentEventShownHidden = true;
//	protected boolean enableComponentEventMoved = true;
//	protected boolean enableComponentEventResized = true;
//	protected boolean enableContainerEventAddedRemoved = true;
//	protected boolean enableFocusEventGainedLost = true;
//	protected boolean enableKeyEventPressedReleased = true;
//	protected boolean enableKeyEventTyped = true;
//	protected boolean enableMouseEventPressedReleased = true;
//	protected boolean enableMouseEventClicked = true;
//	protected boolean enableMouseEventMoved = false;  // <= TOCHECK this one disable by default!!
//	protected boolean enableMouseEventDragged = true;
//	protected boolean enableMouseEventEnteredExited = false; // <= TOCHECK this one disable by default!!
//	protected boolean enableMouseEventWheel = true;
//	protected boolean enableWindowEventOpenedClosingClosed = true;
//	protected boolean enableWindowEventIconifiedDeiconified = true;
//	protected boolean enableWindowEventActivatedDeactivated = true;
//	protected boolean enableWindowEventFocusGainedLost = true;
//	protected boolean enableWindowEventStateChanged = true;
//	protected boolean enableActionEventPerformed = true;
//	protected boolean enableAdjustmentEventValueChanged = true;
//	protected boolean enableItemEventStateChanged = true;
//	protected boolean enableTextEventValueChanged = true;
//	protected boolean enableInputMethodEventTextChanged = true;
//	protected boolean enableInputMethodEventCaretChanged = true;
//	// PaintEvent ?? 
//	protected boolean enableInvocationEventDefault = true;
//	protected boolean enableHierarchyEventAncestorMoved = true;
//	protected boolean enableHierarchyEventAncestorResized = true;
//	protected boolean enableHierarchyEventChanged = true;
	
	
	// ------------------------------------------------------------------------
	
	public AWTEventCaptureCategoryPanel(JotoContext context) {
		super(context, AWTSPY_CAPTURE_CATEGORY);
	}

	@Override
	protected void postInitComponents(GridBagLayoutFormBuilder b) {
		awtSpy = new AWTRecordEventWriterSpy(filterCategoryModel.getResultFilteringEventWriter());
		
		activateAWTToolkitListenerCheckBox = JCheckBoxUtils.snew("Activate AWT Toolkit Listener", false, this, "onActivateAWTToolkitListenerCheckBox");
		b.addCompFillRow(activateAWTToolkitListenerCheckBox);
		
		{ // toolbar
			JToolBar toolbar = new JToolBar();
			toolbar.setFloatable(false);
			
			JButton selectAllButton = JButtonUtils.snew("Select All", this, "onButtonSelectAll");
			toolbar.add(selectAllButton);

			JButton deselectAllButton = JButtonUtils.snew("Deselect All", this, "onButtonDeselectAll");
			toolbar.add(deselectAllButton);

			b.addCompFillRow(toolbar);
		}
		
		{ // checkbox Masks Panel (several columns)
			JPanel masksPanel = new JPanel(new GridBagLayout());
			b.addCompFillRow(masksPanel);
			GridBagLayoutFormBuilder mb = new GridBagLayoutFormBuilder(masksPanel); 
			componentEventMaskCheckBox = JCheckBoxUtils.snew("COMPONENT", false, this, "onComponentEventMaskCheckBox");
			mb.addCompFillRow(componentEventMaskCheckBox);
			containerEventMaskCheckBox = JCheckBoxUtils.snew("CONTAINER", false, this, "onContainerEventMaskCheckBox");
			mb.addCompFillRow(containerEventMaskCheckBox);
			focusEventMaskCheckBox = JCheckBoxUtils.snew("FOCUS", false, this, "onFocusEventMaskCheckBox");
			mb.addCompFillRow(focusEventMaskCheckBox);
			keyEventMaskCheckBox = JCheckBoxUtils.snew("KEY", false, this, "onKeyEventMaskCheckBox");
			mb.addCompFillRow(keyEventMaskCheckBox);
			mouseEventMaskCheckBox = JCheckBoxUtils.snew("MOUSE", false, this, "onMouseEventMaskCheckBox");
			mb.addCompFillRow(mouseEventMaskCheckBox);
			mouseMotionEventMaskCheckBox = JCheckBoxUtils.snew("MOUSE_MOTION", false, this, "onMouseMotionEventMaskCheckBox");
			mb.addCompFillRow(mouseMotionEventMaskCheckBox);
			windowEventMaskCheckBox = JCheckBoxUtils.snew("WINDOW", false, this, "onWindowEventMaskCheckBox");
			mb.addCompFillRow(windowEventMaskCheckBox);
			
			mb.newColumn();
			
			actionEventMaskCheckBox = JCheckBoxUtils.snew("ACTION", false, this, "onActionEventMaskCheckBox");
			mb.addCompFillRow(actionEventMaskCheckBox);
			adjustmentEventMaskCheckBox = JCheckBoxUtils.snew("ADJUSTMENT", false, this, "onAdjustmentEventMaskCheckBox");
			mb.addCompFillRow(adjustmentEventMaskCheckBox);
			itemEventMaskCheckBox = JCheckBoxUtils.snew("ITEM", false, this, "onItemEventMaskCheckBox");
			mb.addCompFillRow(itemEventMaskCheckBox);
			textEventMaskCheckBox = JCheckBoxUtils.snew("TEXT", false, this, "onTextEventMaskCheckBox");
			mb.addCompFillRow(textEventMaskCheckBox);
			inputMethodEventMaskCheckBox = JCheckBoxUtils.snew("INPUT_METH", false, this, "onInputMethodEventMaskCheckBox");
			mb.addCompFillRow(inputMethodEventMaskCheckBox);
			paintEventMaskCheckBox = JCheckBoxUtils.snew("PAINT", false, this, "onPaintEventMaskCheckBox"); 
			mb.addCompFillRow(paintEventMaskCheckBox);
			
			mb.newColumn();
			
			invocationEventMaskCheckBox = JCheckBoxUtils.snew("INVOCATION", false, this, "onInvocationEventMaskCheckBox");
			mb.addCompFillRow(invocationEventMaskCheckBox);
			hierarchyEventMaskCheckBox = JCheckBoxUtils.snew("HIERARCHY", false, this, "onHierarchyEventMaskCheckBox");
			mb.addCompFillRow(hierarchyEventMaskCheckBox);
			hierarchyBoundsEventMaskCheckBox = JCheckBoxUtils.snew("HIERARCHy_BOUND", false, this, "onHierarchyBoundsEventMaskCheckBox");
			mb.addCompFillRow(hierarchyBoundsEventMaskCheckBox);
			mouseWheelEventMaskCheckBox = JCheckBoxUtils.snew("MOUSE_WHEEL", false, this, "onMouseWheelEventMaskCheckBox");
			mb.addCompFillRow(mouseWheelEventMaskCheckBox);
			windowStateEventMaskCheckBox = JCheckBoxUtils.snew("WINDOW_STATE", false, this, "onWindowStateEventMaskCheckBox");
			mb.addCompFillRow(windowStateEventMaskCheckBox);
			windowFocusEventMaskCheckBox = JCheckBoxUtils.snew("WINDOW_FOCUS", false, this, "onWindowFocusEventMaskCheckBox");
			mb.addCompFillRow(windowFocusEventMaskCheckBox);
		}
		
		eventMaskCheckBoxes = new JCheckBox[] {
				componentEventMaskCheckBox,
				containerEventMaskCheckBox,
				focusEventMaskCheckBox,
				keyEventMaskCheckBox,
				mouseEventMaskCheckBox,
				windowEventMaskCheckBox,
				actionEventMaskCheckBox,
				adjustmentEventMaskCheckBox,
				itemEventMaskCheckBox,
				textEventMaskCheckBox,
				inputMethodEventMaskCheckBox,
				paintEventMaskCheckBox, 
				invocationEventMaskCheckBox,
				hierarchyEventMaskCheckBox,
				hierarchyBoundsEventMaskCheckBox,
				mouseWheelEventMaskCheckBox,
				windowStateEventMaskCheckBox,
				windowFocusEventMaskCheckBox
		};
	}

	
	// ------------------------------------------------------------------------
	
	private static long toggleFlag(long mask, long flag, boolean isSetFlag) {
		long res = (isSetFlag)? (mask | flag) : (mask & (~flag));
		log.debug("toggle AWTSpy event mask:" + (isSetFlag? "set" : "clear") + AWTRecordEventWriterSpy.maskToString(flag)
				+ " => " + AWTRecordEventWriterSpy.maskToString(res)
				+ " (old:" + AWTRecordEventWriterSpy.maskToString(mask) + ")"
				); 
		return res;
	}

	/** called by introspection, GUI callback */
	public void onActivateAWTToolkitListenerCheckBox(ActionEvent event) {
		awtSpy.setEnable(activateAWTToolkitListenerCheckBox.isSelected());
	}
	
	/** called by introspection, GUI callback */
	public void onButtonSelectAll(ActionEvent event) {
		awtSpy.setAwtEventMask(AWTRecordEventWriterSpy.ALL_AWTEVENTS_MASK);
		for (JCheckBox cb : eventMaskCheckBoxes) {
			cb.setSelected(true);
		}
	}
	
	/** called by introspection, GUI callback */
	public void onButtonDeselectAll(ActionEvent event) {
		awtSpy.setAwtEventMask(0);
		for (JCheckBox cb : eventMaskCheckBoxes) {
			cb.setSelected(false);
		}
	}
	
	
	/** called by introspection, GUI callback */
	public void onComponentEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.COMPONENT_EVENT_MASK, componentEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}

	/** called by introspection, GUI callback */
	public void onContainerEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.CONTAINER_EVENT_MASK, containerEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}

	/** called by introspection, GUI callback */
	public void onFocusEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.FOCUS_EVENT_MASK, focusEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}

	/** called by introspection, GUI callback */
	public void onKeyEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.KEY_EVENT_MASK, keyEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onMouseEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.MOUSE_EVENT_MASK, mouseEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}

	/** called by introspection, GUI callback */
	public void onMouseMotionEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.MOUSE_MOTION_EVENT_MASK, mouseEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}

	/** called by introspection, GUI callback */
	public void onWindowEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.WINDOW_EVENT_MASK, windowEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onActionEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.ACTION_EVENT_MASK, actionEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onAdjustmentEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.ADJUSTMENT_EVENT_MASK, adjustmentEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onItemEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.ITEM_EVENT_MASK, itemEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onTextEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.TEXT_EVENT_MASK, textEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onInputMethodEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.INPUT_METHOD_EVENT_MASK, inputMethodEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onPaintEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.PAINT_EVENT_MASK, paintEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onInvocationEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.INVOCATION_EVENT_MASK, invocationEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onHierarchyEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.HIERARCHY_EVENT_MASK, hierarchyEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onHierarchyBoundsEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK, hierarchyBoundsEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onMouseWheelEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.MOUSE_WHEEL_EVENT_MASK, mouseWheelEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onWindowStateEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.WINDOW_STATE_EVENT_MASK, windowStateEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
	/** called by introspection, GUI callback */
	public void onWindowFocusEventMaskCheckBox(ActionEvent event) {
		long mask = toggleFlag(awtSpy.getAwtEventMask(), AWTEvent.WINDOW_FOCUS_EVENT_MASK, windowFocusEventMaskCheckBox.isSelected());
		awtSpy.setAwtEventMask(mask);
	}
	
}
