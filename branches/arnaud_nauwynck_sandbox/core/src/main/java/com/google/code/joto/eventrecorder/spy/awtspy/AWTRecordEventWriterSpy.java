package com.google.code.joto.eventrecorder.spy.awtspy;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.TextEvent;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Date;

/**
 * a spy to listen AWT event, and write them as RecordEventSummary 
 * into a RecordEventWriter
 * 
 */
public class AWTRecordEventWriterSpy {

	private static Logger log = LoggerFactory.getLogger(AWTRecordEventWriterSpy.class);

	public static final long ALL_AWTEVENTS_MASK =
			AWTEvent.COMPONENT_EVENT_MASK
			| AWTEvent.CONTAINER_EVENT_MASK // cf ContainerEvent
			| AWTEvent.FOCUS_EVENT_MASK // cf FocusEvent
			| AWTEvent.KEY_EVENT_MASK
			| AWTEvent.MOUSE_EVENT_MASK
			| AWTEvent.MOUSE_MOTION_EVENT_MASK
			| AWTEvent.WINDOW_EVENT_MASK
			| AWTEvent.ACTION_EVENT_MASK
			| AWTEvent.ADJUSTMENT_EVENT_MASK
			| AWTEvent.ITEM_EVENT_MASK
			| AWTEvent.TEXT_EVENT_MASK
			| AWTEvent.INPUT_METHOD_EVENT_MASK
			// | AWTEvent.INPUT_METHODS_ENABLED_MASK
			| AWTEvent.PAINT_EVENT_MASK 
			| AWTEvent.INVOCATION_EVENT_MASK
			| AWTEvent.HIERARCHY_EVENT_MASK
			| AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK
			| AWTEvent.MOUSE_WHEEL_EVENT_MASK
			| AWTEvent.WINDOW_STATE_EVENT_MASK
			| AWTEvent.WINDOW_FOCUS_EVENT_MASK;

	/**
	 * info for AWTEvent
	 */
	public static enum AWTEventInfo {
		
		// ComponentEvent
		COMPONENT_SHOWN(ComponentEvent.COMPONENT_SHOWN, "COMPONENT_SHOWN", "ComponentEvent", "Shown"),
		COMPONENT_HIDDEN(ComponentEvent.COMPONENT_HIDDEN, "COMPONENT_HIDDEN", "ComponentEvent", "Hidden"),
		COMPONENT_MOVED(ComponentEvent.COMPONENT_MOVED, "COMPONENT_MOVED", "ComponentEvent", "Moved"),
		COMPONENT_RESIZED(ComponentEvent.COMPONENT_RESIZED, "COMPONENT_RESIZED", "ComponentEvent", "Resized"),

		// ContainerEvent
		COMPONENT_ADDED(ContainerEvent.COMPONENT_ADDED, "COMPONENT_ADDED", "ContainerEvent", "Added"),
		COMPONENT_REMOVED(ContainerEvent.COMPONENT_REMOVED, "COMPONENT_REMOVED", "ContainerEvent", "Removed"),

		// FocusEvent
		FOCUS_GAINED(FocusEvent.FOCUS_GAINED, "FOCUS_GAINED", "FocusEvent", "FocusGained"),
		FOCUS_LOST(FocusEvent.FOCUS_LOST, "FOCUS_LOST", "FocusEvent", "FocusLost"),

		// KeyEvent
		KEY_PRESSED(KeyEvent.KEY_PRESSED, "KEY_PRESSED", "KeyEvent", "Pressed"),
		KEY_RELEASED(KeyEvent.KEY_RELEASED, "KEY_RELEASED", "KeyEvent", "Released"),
		KEY_TYPED(KeyEvent.KEY_TYPED, "KEY_TYPED", "KeyEvent", "Typed"),

		// MouseEvent
		MOUSE_PRESSED(MouseEvent.MOUSE_PRESSED, "MOUSE_PRESSED", "MouseEvent", "Pressed"),
		MOUSE_RELEASED(MouseEvent.MOUSE_RELEASED, "MOUSE_RELEASED", "MouseEvent", "Released"),
		MOUSE_CLICKED(MouseEvent.MOUSE_CLICKED, "MOUSE_CLICKED", "MouseEvent", "Clicked"),
		MOUSE_MOVED(MouseEvent.MOUSE_MOVED, "MOUSE_MOVED", "MouseEvent", "Moved"),
		MOUSE_DRAGGED(MouseEvent.MOUSE_DRAGGED, "MOUSE_DRAGGED", "MouseEvent", "Dragged"),
		MOUSE_ENTERED(MouseEvent.MOUSE_ENTERED, "MOUSE_ENTERED", "MouseEvent", "Eventered"),
		MOUSE_EXITED(MouseEvent.MOUSE_EXITED, "MOUSE_EXITED", "MouseEvent", "Exited"),
		MOUSE_WHEEL(MouseEvent.MOUSE_WHEEL, "MOUSE_WHEEL", "MouseEvent", "Wheel"),

		// WindowEvent
		WINDOW_OPENED(WindowEvent.WINDOW_OPENED, "WINDOW_OPENED", "WindowEvent", "Opened"),
		WINDOW_CLOSING(WindowEvent.WINDOW_CLOSING, "WINDOW_CLOSING", "WindowEvent", "Closing"),
		WINDOW_CLOSED(WindowEvent.WINDOW_CLOSED, "WINDOW_CLOSED", "WindowEvent", "Closed"),
		WINDOW_ICONIFIED(WindowEvent.WINDOW_ICONIFIED, "WINDOW_ICONIFIED", "WindowEvent", "Iconified"),
		WINDOW_DEICONIFIED(WindowEvent.WINDOW_DEICONIFIED, "WINDOW_DEICONIFIED", "WindowEvent", "Deiconified"),
		WINDOW_ACTIVATED(WindowEvent.WINDOW_ACTIVATED, "WINDOW_ACTIVATED", "WindowEvent", "Activated"),
		WINDOW_DEACTIVATED(WindowEvent.WINDOW_DEACTIVATED, "WINDOW_DEACTIVATED", "WindowEvent", "Deactivated"),
		WINDOW_GAINED_FOCUS(WindowEvent.WINDOW_GAINED_FOCUS, "WINDOW_GAINED_FOCUS", "WindowEvent", "GainedFocus"),
		WINDOW_LOST_FOCUS(WindowEvent.WINDOW_LOST_FOCUS, "WINDOW_LOST_FOCUS", "WindowEvent", "LostFocus"),
		WINDOW_STATE_CHANGED(WindowEvent.WINDOW_STATE_CHANGED, "WINDOW_STATE_CHANGED", "WindowEvent", "StateChanged"),

		// ActionEvent
		ACTION_PERFORMED(ActionEvent.ACTION_PERFORMED, "ACTION_PERFORMED", "ActionEvent", "Performed"),

		// AdjustmentEvent
		ADJUSTMENT_VALUE_CHANGED(AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED, "ADJUSTMENT_VALUE_CHANGED", "AdjustmentEvent", "ValueChanged"),

		// ItemEvent
		ITEM_STATE_CHANGED(ItemEvent.ITEM_STATE_CHANGED, "ITEM_STATE_CHANGED", "ItemEvent", "StateChanged"),

		// TextEvent
		TEXT_VALUE_CHANGED(TextEvent.TEXT_VALUE_CHANGED, "TEXT_VALUE_CHANGED", "TextEvent", "ValueChanged"),

		// InputMethodEvent
		INPUT_METHOD_TEXT_CHANGED(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, "INPUT_METHOD_TEXT_CHANGED", "InputMethodEvent", "TextChanged"),
		CARET_POSITION_CHANGED(InputMethodEvent.CARET_POSITION_CHANGED, "CARET_POSITION_CHANGED", "InputMethodEvent", "CaretPosChanged"),

		// PaintEvent ?? 

		// InvocationEvent, INVOCATION_EVENT_MASK 
		INVOCATION_DEFAULT(InvocationEvent.INVOCATION_DEFAULT, "INVOCATION_DEFAULT", "InvocationEvent", "Default"),

		// HierarchyEvent, HIERARCHY_EVENT_MASK
		ANCESTOR_MOVED(HierarchyEvent.ANCESTOR_MOVED, "ANCESTOR_MOVED", "HierarchyEvent", "Moved"),
		ANCESTOR_RESIZED(HierarchyEvent.ANCESTOR_RESIZED, "ANCESTOR_RESIZED", "HierarchyEvent", "Resized"),
		HIERARCHY_CHANGED(HierarchyEvent.HIERARCHY_CHANGED, "HIERARCHY_CHANGED", "HierarchyEvent", "Changed");
		
		private final long id;
		private final String eventName;
		private final String eventClassName;
		private final String eventMethodName;
		  
		private AWTEventInfo(long id, String eventName, String eventClassName, String eventMethodName) {
			this.id = id;
			this.eventName = eventName;
			this.eventClassName = eventClassName;
			this.eventMethodName = eventMethodName;
		}

		public long getId() {
			return id;
		}

		public String getEventName() {
			return eventName;
		}

		public String getEventClassName() {
			return eventClassName;
		}

		public String getEventMethodName() {
			return eventMethodName;
		}
		
	}
	
	/**
	 * info for AWTEvent MASK
	 */
	public static enum AWTEventMaskInfo {

		COMPONENT_EVENT_MASK(AWTEvent.COMPONENT_EVENT_MASK, "COMPONENT_EVENT_MASK", "ComponentEvent", //
				AWTEventInfo.COMPONENT_SHOWN, AWTEventInfo.COMPONENT_HIDDEN, AWTEventInfo.COMPONENT_MOVED, AWTEventInfo.COMPONENT_RESIZED),
		CONTAINER_EVENT_MASK(AWTEvent.CONTAINER_EVENT_MASK, "CONTAINER_EVENT_MASK", "ContainerEvent", //
				AWTEventInfo.COMPONENT_ADDED, AWTEventInfo.COMPONENT_REMOVED),
		FOCUS_EVENT_MASK(AWTEvent.FOCUS_EVENT_MASK, "FOCUS_EVENT_MASK", "FocusEvent", //
				AWTEventInfo.FOCUS_GAINED, AWTEventInfo.FOCUS_LOST),
		KEY_EVENT_MASK(AWTEvent.KEY_EVENT_MASK, "KEY_EVENT_MASK", "KeyEvent", //
				AWTEventInfo.KEY_PRESSED, AWTEventInfo.KEY_RELEASED, AWTEventInfo.KEY_TYPED),
		MOUSE_EVENT_MASK(AWTEvent.MOUSE_EVENT_MASK, "MOUSE_EVENT_MASK", "MouseEvent", //
				AWTEventInfo.MOUSE_PRESSED, AWTEventInfo.MOUSE_RELEASED, AWTEventInfo.MOUSE_CLICKED, 
				AWTEventInfo.MOUSE_MOVED, AWTEventInfo.MOUSE_DRAGGED, 
				AWTEventInfo.MOUSE_ENTERED, AWTEventInfo.MOUSE_EXITED, 
				AWTEventInfo.MOUSE_WHEEL),
		MOUSE_MOTION_EVENT_MASK(AWTEvent.MOUSE_MOTION_EVENT_MASK, "MOUSE_MOTION_EVENT_MASK", "MouseEvent" //
				),
		WINDOW_EVENT_MASK(AWTEvent.WINDOW_EVENT_MASK, "WINDOW_EVENT_MASK", "WindowEvent", //
				AWTEventInfo.WINDOW_OPENED, AWTEventInfo.WINDOW_CLOSING, AWTEventInfo.WINDOW_CLOSED,
				AWTEventInfo.WINDOW_ICONIFIED, AWTEventInfo.WINDOW_DEICONIFIED, 
				AWTEventInfo.WINDOW_ACTIVATED, AWTEventInfo.WINDOW_DEACTIVATED,
				AWTEventInfo.WINDOW_GAINED_FOCUS, AWTEventInfo.WINDOW_LOST_FOCUS, 
				AWTEventInfo.WINDOW_STATE_CHANGED),
		ACTION_EVENT_MASK(AWTEvent.ACTION_EVENT_MASK, "ACTION_EVENT_MASK", "ActionEvent", //
				AWTEventInfo.ACTION_PERFORMED),
		ADJUSTMENT_EVENT_MASK(AWTEvent.ADJUSTMENT_EVENT_MASK, "ADJUSTMENT_EVENT_MASK", "AdjustmentEvent", //
				AWTEventInfo.ADJUSTMENT_VALUE_CHANGED),
		ITEM_EVENT_MASK(AWTEvent.ITEM_EVENT_MASK, "ITEM_EVENT_MASK", "ItemEvent", //
				AWTEventInfo.ITEM_STATE_CHANGED),
		TEXT_EVENT_MASK(AWTEvent.TEXT_EVENT_MASK, "TEXT_EVENT_MASK", "TextEvent", //
				AWTEventInfo.TEXT_VALUE_CHANGED),
		INPUT_METHOD_EVENT_MASK(AWTEvent.INPUT_METHOD_EVENT_MASK, "INPUT_METHOD_EVENT_MASK", "InputMethodEvent", //
				AWTEventInfo.INPUT_METHOD_TEXT_CHANGED, AWTEventInfo.CARET_POSITION_CHANGED),
		PAINT_EVENT_MASK(AWTEvent.PAINT_EVENT_MASK, "PAINT_EVENT_MASK", "PainEvent" //
				), 
		INVOCATION_EVENT_MASK(AWTEvent.INVOCATION_EVENT_MASK, "INVOCATION_EVENT_MASK", "InvocationEvent", //
				AWTEventInfo.INVOCATION_DEFAULT),
		HIERARCHY_EVENT_MASK(AWTEvent.HIERARCHY_EVENT_MASK, "HIERARCHY_EVENT_MASK", "HierarchyEvent",
				AWTEventInfo.ANCESTOR_MOVED, AWTEventInfo.ANCESTOR_RESIZED, AWTEventInfo.HIERARCHY_CHANGED),
		HIERARCHY_BOUNDS_EVENT_MASK(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK, "HIERARCHY_BOUNDS_EVENT_MASK", "HierarchyEvent" //
				),
		MOUSE_WHEEL_EVENT_MASK(AWTEvent.MOUSE_WHEEL_EVENT_MASK, "MOUSE_WHEEL_EVENT_MASK", "MouseEvent" //
				),
		WINDOW_STATE_EVENT_MASK(AWTEvent.WINDOW_STATE_EVENT_MASK, "WINDOW_STATE_EVENT_MASK", "WindowEvent" //
				),
		WINDOW_FOCUS_EVENT_MASK(AWTEvent.WINDOW_FOCUS_EVENT_MASK, "WINDOW_FOCUS_EVENT_MASK", "WindowEvent" // 
				),;


		private final long flag;
		private final String flagName;
		private final String eventClassName;
		private final AWTEventInfo[] eventInfos;
		
		AWTEventMaskInfo(long flag, String flagName, String eventClassName, AWTEventInfo... eventInfos) {
			 this.flag = flag;
			 this.flagName = flagName;
			 this.eventClassName = eventClassName;
			 this.eventInfos = eventInfos;
		}
		
		public long getFlag() {
			return flag;
		}
		public String getFlagName() {
			return flagName;
		}
		public String getEventClassName() {
			return eventClassName;
		}
		public AWTEventInfo[] getEventInfos() {
			return eventInfos;
		}
		
	}
	
	
	
	// ------------------------------------------------------------------------

	protected RecordEventWriter eventWriter;

	protected boolean enable = false;

	protected AWTEventListener innerAWTEventListener;

	protected long awtEventMask = 0; 

	// sub-events enable/disable flags, per group of eventTypes (or per eventIDs)
	
	protected boolean enableComponentEventShownHidden = true;
	protected boolean enableComponentEventMoved = true;
	protected boolean enableComponentEventResized = true;
	protected boolean enableContainerEventAddedRemoved = true;
	protected boolean enableFocusEventGainedLost = true;
	protected boolean enableKeyEventPressedReleased = true;
	protected boolean enableKeyEventTyped = true;
	protected boolean enableMouseEventPressedReleased = true;
	protected boolean enableMouseEventClicked = true;
	protected boolean enableMouseEventMoved = false;  // <= TOCHECK this one disable by default!!
	protected boolean enableMouseEventDragged = true;
	protected boolean enableMouseEventEnteredExited = false; // <= TOCHECK this one disable by default!!
	protected boolean enableMouseEventWheel = true;
	protected boolean enableWindowEventOpenedClosingClosed = true;
	protected boolean enableWindowEventIconifiedDeiconified = true;
	protected boolean enableWindowEventActivatedDeactivated = true;
	protected boolean enableWindowEventFocusGainedLost = true;
	protected boolean enableWindowEventStateChanged = true;
	protected boolean enableActionEventPerformed = true;
	protected boolean enableAdjustmentEventValueChanged = true;
	protected boolean enableItemEventStateChanged = true;
	protected boolean enableTextEventValueChanged = true;
	protected boolean enableInputMethodEventTextChanged = true;
	protected boolean enableInputMethodEventCaretChanged = true;
	// PaintEvent ?? 
	protected boolean enableInvocationEventDefault = true;
	protected boolean enableHierarchyEventAncestorMoved = true;
	protected boolean enableHierarchyEventAncestorResized = true;
	protected boolean enableHierarchyEventChanged = true;
	
	
	
	// ------------------------------------------------------------------------

	public AWTRecordEventWriterSpy(RecordEventWriter eventWriter) {
		this.eventWriter = eventWriter;
		this.innerAWTEventListener = new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				onAWTEventDispatched(event);
			}
		};
	}

	// ------------------------------------------------------------------------

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean p) {
		if (p != enable) {
			if (enable && (awtEventMask != 0)) {
				uninstallAWTEventListener();
			}
			this.enable = p;
			if (enable && (awtEventMask != 0)) {
				installAWTEventListener();
			}
		}
	}
	
	public long getAwtEventMask() {
		return awtEventMask;
	}

	public void setAwtEventMask(long p) {
		if (p != awtEventMask) {
			if (enable && (awtEventMask != 0)) {
				uninstallAWTEventListener();
			}
			this.awtEventMask = p;
			if (enable && (awtEventMask != 0)) {
				installAWTEventListener();
			}
		}
	}

	public static String maskToString(long mask) {
		StringBuilder sb = new StringBuilder();
		for (AWTEventMaskInfo flagInfo : AWTEventMaskInfo.values()) {
			if (0 != (mask & flagInfo.getFlag())) {
				sb.append(flagInfo.getFlagName() + " |");
			}
		}
		if (sb.length() != 0) {
			sb.delete(sb.length()-2, sb.length());
		}
		return sb.toString();
	}
	
	
	
	// ------------------------------------------------------------------------
	
	public boolean isEnableComponentEventShownHidden() {
		return enableComponentEventShownHidden;
	}

	public void setEnableComponentEventShownHidden(boolean p) {
		this.enableComponentEventShownHidden = p;
	}

	public boolean isEnableComponentEventMoved() {
		return enableComponentEventMoved;
	}

	public void setEnableComponentEventMoved(boolean p) {
		this.enableComponentEventMoved = p;
	}

	public boolean isEnableComponentEventResized() {
		return enableComponentEventResized;
	}

	public void setEnableComponentEventResized(boolean p) {
		this.enableComponentEventResized = p;
	}

	public boolean isEnableContainerEventAddedRemoved() {
		return enableContainerEventAddedRemoved;
	}

	public void setEnableContainerEventAddedRemoved(boolean p) {
		this.enableContainerEventAddedRemoved = p;
	}

	public boolean isEnableFocusEventGainedLost() {
		return enableFocusEventGainedLost;
	}

	public void setEnableFocusEventGainedLost(boolean p) {
		this.enableFocusEventGainedLost = p;
	}

	public boolean isEnableKeyEventPressedReleased() {
		return enableKeyEventPressedReleased;
	}

	public void setEnableKeyEventPressedReleased(boolean p) {
		this.enableKeyEventPressedReleased = p;
	}

	public boolean isEnableKeyEventTyped() {
		return enableKeyEventTyped;
	}

	public void setEnableKeyEventTyped(boolean p) {
		this.enableKeyEventTyped = p;
	}

	public boolean isEnableMouseEventPressedReleased() {
		return enableMouseEventPressedReleased;
	}

	public void setEnableMouseEventPressedReleased(boolean p) {
		this.enableMouseEventPressedReleased = p;
	}

	public boolean isEnableMouseEventClicked() {
		return enableMouseEventClicked;
	}

	public void setEnableMouseEventClicked(boolean p) {
		this.enableMouseEventClicked = p;
	}

	public boolean isEnableMouseEventMoved() {
		return enableMouseEventMoved;
	}

	public void setEnableMouseEventMoved(boolean p) {
		this.enableMouseEventMoved = p;
	}

	public boolean isEnableMouseEventDragged() {
		return enableMouseEventDragged;
	}

	public void setEnableMouseEventDragged(boolean p) {
		this.enableMouseEventDragged = p;
	}

	public boolean isEnableMouseEventEnteredExited() {
		return enableMouseEventEnteredExited;
	}

	public void setEnableMouseEventEnteredExited(boolean p) {
		this.enableMouseEventEnteredExited = p;
	}

	public boolean isEnableMouseEventWheel() {
		return enableMouseEventWheel;
	}

	public void setEnableMouseEventWheel(boolean p) {
		this.enableMouseEventWheel = p;
	}

	public boolean isEnableWindowEventOpenedClosingClosed() {
		return enableWindowEventOpenedClosingClosed;
	}

	public void setEnableWindowEventOpenedClosingClosed(boolean p) {
		this.enableWindowEventOpenedClosingClosed = p;
	}

	public boolean isEnableWindowEventIconifiedDeiconified() {
		return enableWindowEventIconifiedDeiconified;
	}

	public void setEnableWindowEventIconifiedDeiconified(boolean p) {
		this.enableWindowEventIconifiedDeiconified = p;
	}

	public boolean isEnableWindowEventActivatedDeactivated() {
		return enableWindowEventActivatedDeactivated;
	}

	public void setEnableWindowEventActivatedDeactivated(boolean p) {
		this.enableWindowEventActivatedDeactivated = p;
	}

	public boolean isEnableWindowEventFocusGainedLost() {
		return enableWindowEventFocusGainedLost;
	}

	public void setEnableWindowEventFocusGainedLost(boolean p) {
		this.enableWindowEventFocusGainedLost = p;
	}

	public boolean isEnableWindowEventStateChanged() {
		return enableWindowEventStateChanged;
	}

	public void setEnableWindowEventStateChanged(boolean p) {
		this.enableWindowEventStateChanged = p;
	}

	public boolean isEnableActionEventPerformed() {
		return enableActionEventPerformed;
	}

	public void setEnableActionEventPerformed(boolean p) {
		this.enableActionEventPerformed = p;
	}

	public boolean isEnableAdjustmentEventValueChanged() {
		return enableAdjustmentEventValueChanged;
	}

	public void setEnableAdjustmentEventValueChanged(boolean p) {
		this.enableAdjustmentEventValueChanged = p;
	}

	public boolean isEnableItemEventStateChanged() {
		return enableItemEventStateChanged;
	}

	public void setEnableItemEventStateChanged(boolean p) {
		this.enableItemEventStateChanged = p;
	}

	public boolean isEnableTextEventValueChanged() {
		return enableTextEventValueChanged;
	}

	public void setEnableTextEventValueChanged(boolean p) {
		this.enableTextEventValueChanged = p;
	}

	public boolean isEnableInputMethodEventTextChanged() {
		return enableInputMethodEventTextChanged;
	}

	public void setEnableInputMethodEventTextChanged(boolean p) {
		this.enableInputMethodEventTextChanged = p;
	}

	public boolean isEnableInputMethodEventCaretChanged() {
		return enableInputMethodEventCaretChanged;
	}

	public void setEnableInputMethodEventCaretChanged(boolean p) {
		this.enableInputMethodEventCaretChanged = p;
	}

	public boolean isEnableInvocationEventDefault() {
		return enableInvocationEventDefault;
	}

	public void setEnableInvocationEventDefault(boolean p) {
		this.enableInvocationEventDefault = p;
	}

	public boolean isEnableHierarchyEventAncestorMoved() {
		return enableHierarchyEventAncestorMoved;
	}

	public void setEnableHierarchyEventAncestorMoved(boolean p) {
		this.enableHierarchyEventAncestorMoved = p;
	}

	public boolean isEnableHierarchyEventAncestorResized() {
		return enableHierarchyEventAncestorResized;
	}

	public void setEnableHierarchyEventAncestorResized(boolean p) {
		this.enableHierarchyEventAncestorResized = p;
	}

	public boolean isEnableHierarchyEventChanged() {
		return enableHierarchyEventChanged;
	}

	public void setEnableHierarchyEventChanged(boolean p) {
		this.enableHierarchyEventChanged = p;
	}

	private void installAWTEventListener() {
		log.info("add AWTEventListener for joto event writer: mask=" + awtEventMask + " (" + maskToString(awtEventMask) + ")");
		try {
			java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(
					innerAWTEventListener, awtEventMask);
		} catch (SecurityException ex) {
			log.error("Failed to add AWTEventListener ... ignore!", ex);
		}
	}

	private void uninstallAWTEventListener() {
		log.info("remove AWTEventListener for joto event writer");
		java.awt.Toolkit.getDefaultToolkit().removeAWTEventListener(
				innerAWTEventListener);
	}

	private void onAWTEventDispatched(AWTEvent event) {
		if (!enable) {
			return;
		}
		
		// TODO ... NOT IMPLEMENTED YET
		
		String typeStr;
		String eventClassName;
		String eventMethodName;
		String eventMethodDetail = null;

		Serializable eventData = null;

		boolean enableEvent;
		

		switch (event.getID()) {

		// ComponentEvent
		case ComponentEvent.COMPONENT_SHOWN:
			enableEvent = enableComponentEventShownHidden;
			typeStr = "COMPONENT_SHOWN";
			eventClassName = "ComponentEvent";
			eventMethodName = "Shown";
			break;
		case ComponentEvent.COMPONENT_HIDDEN:
			enableEvent = enableComponentEventShownHidden;
			typeStr = "COMPONENT_HIDDEN";
			eventClassName = "ComponentEvent";
			eventMethodName = "Hidden";
			break;
		case ComponentEvent.COMPONENT_MOVED:
			enableEvent = enableComponentEventMoved;
			typeStr = "COMPONENT_MOVED";
			// b.x+","+b.y+" "+b.width+"x"+b.height;
			eventClassName = "ComponentEvent";
			eventMethodName = "Moved";
			break;
		case ComponentEvent.COMPONENT_RESIZED:
			enableEvent = enableComponentEventResized;
			typeStr = "COMPONENT_RESIZED";
			// b.x+","+b.y+" "+b.width+"x"+b.height+;
			eventClassName = "ComponentEvent";
			eventMethodName = "Resized";
			break;

		// ContainerEvent
        case ContainerEvent.COMPONENT_ADDED:
        	enableEvent = enableContainerEventAddedRemoved;
            typeStr = "COMPONENT_ADDED";
			eventClassName = "ContainerEvent";
			eventMethodName = "CompAdded";
            break;
        case ContainerEvent.COMPONENT_REMOVED:
        	enableEvent = enableContainerEventAddedRemoved;
            typeStr = "COMPONENT_REMOVED";
			eventClassName = "ContainerEvent";
			eventMethodName = "CompRemoved";
            break;

        // FocusEvent 
        case FocusEvent.FOCUS_GAINED:
            enableEvent = enableFocusEventGainedLost;
            typeStr = "FOCUS_GAINED";
			eventClassName = "FocusEvent";
			eventMethodName = "FocusGained";
    		// eventMethodDetail = (event.temporary ? ",temporary" : ",permanent") + ",opposite=" + event.getOppositeComponent();
            break;
        case FocusEvent.FOCUS_LOST:
            enableEvent = enableFocusEventGainedLost;
            typeStr = "FOCUS_LOST";
			eventClassName = "FocusEvent";
			eventMethodName = "FocusLost";
    		// eventMethodDetail = (event.temporary ? ",temporary" : ",permanent") + ",opposite=" + event.getOppositeComponent();
            break;
	
		// KeyEvent
		case KeyEvent.KEY_PRESSED:
    		enableEvent = enableKeyEventPressedReleased;
			typeStr = "KEY_PRESSED";
			eventClassName = "KeyEvent";
			eventMethodName = "Pressed";
			break;
		case KeyEvent.KEY_RELEASED:
    		enableEvent = enableKeyEventPressedReleased;
			typeStr = "KEY_RELEASED";
			eventClassName = "KeyEvent";
			eventMethodName = "Released";
			break;
		case KeyEvent.KEY_TYPED:
    		enableEvent = enableKeyEventTyped;
			typeStr = "KEY_TYPED";
			eventClassName = "KeyEvent";
			eventMethodName = "Typed";
			break;

		// MouseEvent 
		case MouseEvent.MOUSE_PRESSED:
    		enableEvent = enableMouseEventPressedReleased;
			typeStr = "MOUSE_PRESSED";
			eventClassName = "MouseEvent";
			eventMethodName = "Pressed";
			break;
		case MouseEvent.MOUSE_RELEASED:
    		enableEvent = enableMouseEventPressedReleased;
			typeStr = "MOUSE_RELEASED";
			eventClassName = "MouseEvent";
			eventMethodName = "Released";
			break;
		case MouseEvent.MOUSE_CLICKED:
    		enableEvent = enableMouseEventClicked;
			typeStr = "MOUSE_CLICKED";
			eventClassName = "MouseEvent";
			eventMethodName = "Clicked";
			break;
		case MouseEvent.MOUSE_MOVED:
    		enableEvent = enableMouseEventMoved;
			typeStr = "MOUSE_MOVED";
			eventClassName = "MouseEvent";
			eventMethodName = "Moved";
			break;
		case MouseEvent.MOUSE_DRAGGED:
    		enableEvent = enableMouseEventDragged;
			typeStr = "MOUSE_DRAGGED";
			eventClassName = "MouseEvent";
			eventMethodName = "Dragged";
			break;
		case MouseEvent.MOUSE_ENTERED:
    		enableEvent = enableMouseEventEnteredExited;
			typeStr = "MOUSE_ENTERED";
			eventClassName = "MouseEvent";
			eventMethodName = "Entered";
			break;
		case MouseEvent.MOUSE_EXITED:
    		enableEvent = enableMouseEventEnteredExited;
			typeStr = "MOUSE_EXITED";
			eventClassName = "MouseEvent";
			eventMethodName = "Exited";
			break;
		case MouseEvent.MOUSE_WHEEL: // cf also MouseWheelEvent
    		enableEvent = enableMouseEventWheel;
			typeStr = "MOUSE_WHEEL";
			eventClassName = "MouseEvent";
			eventMethodName = "Wheel";
			break;

		// WindowEvent
        case WindowEvent.WINDOW_OPENED:
    		enableEvent = enableWindowEventOpenedClosingClosed;
            typeStr = "WINDOW_OPENED";
			eventClassName = "WindowEvent";
			eventMethodName = "Opened";
            break;
        case WindowEvent.WINDOW_CLOSING:
    		enableEvent = enableWindowEventOpenedClosingClosed;
            typeStr = "WINDOW_CLOSING";
			eventClassName = "WindowEvent";
			eventMethodName = "Closing";
            break;
        case WindowEvent.WINDOW_CLOSED:
    		enableEvent = enableWindowEventOpenedClosingClosed;
            typeStr = "WINDOW_CLOSED";
			eventClassName = "WindowEvent";
			eventMethodName = "Closed";
            break;
        case WindowEvent.WINDOW_ICONIFIED:
    		enableEvent = enableWindowEventIconifiedDeiconified;
            typeStr = "WINDOW_ICONIFIED";
			eventClassName = "WindowEvent";
			eventMethodName = "Iconified";
            break;
        case WindowEvent.WINDOW_DEICONIFIED:
    		enableEvent = enableWindowEventIconifiedDeiconified;
            typeStr = "WINDOW_DEICONIFIED";
			eventClassName = "WindowEvent";
			eventMethodName = "Deiconified";
            break;
        case WindowEvent.WINDOW_ACTIVATED:
    		enableEvent = enableWindowEventActivatedDeactivated;
            typeStr = "WINDOW_ACTIVATED";
			eventClassName = "WindowEvent";
			eventMethodName = "Activated";
            break;
        case WindowEvent.WINDOW_DEACTIVATED:
    		enableEvent = enableWindowEventActivatedDeactivated;
            typeStr = "WINDOW_DEACTIVATED";
			eventClassName = "WindowEvent";
			eventMethodName = "Deactivated";
            break;
        case WindowEvent.WINDOW_GAINED_FOCUS:
    		enableEvent = enableWindowEventFocusGainedLost;
        	typeStr = "WINDOW_GAINED_FOCUS";
			eventClassName = "WindowEvent";
			eventMethodName = "GainedFocus";
        	break;
        case WindowEvent.WINDOW_LOST_FOCUS:
    		enableEvent = enableWindowEventFocusGainedLost;
        	typeStr = "WINDOW_LOST_FOCUS";
			eventClassName = "WindowEvent";
			eventMethodName = "LostFocus";
        	break;
        case WindowEvent.WINDOW_STATE_CHANGED:
    		enableEvent = enableWindowEventStateChanged;
        	typeStr = "WINDOW_STATE_CHANGED";
			eventClassName = "WindowEvent";
			eventMethodName = "StateChanged";
        	break;

        // ActionEvent
        case ActionEvent.ACTION_PERFORMED:
    		enableEvent = enableActionEventPerformed;
            typeStr = "ACTION_PERFORMED";
			eventClassName = "ActionEvent";
			eventMethodName = "Performed";
            break;

        // AdjustmentEvent
        case AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED: {
    		enableEvent = enableAdjustmentEventValueChanged;
            typeStr = "ADJUSTMENT_VALUE_CHANGED";
			eventClassName = "AdjustmentEvent";
			eventMethodName = "AdjustmentValueChanged";
			AdjustmentEvent e = (AdjustmentEvent) event;
		    String adjTypeStr;
		    switch(e.getAdjustmentType()) {
		    case AdjustmentEvent.UNIT_INCREMENT:
		    	adjTypeStr = "UNIT_INCREMENT";
		    	break;
		    case AdjustmentEvent.UNIT_DECREMENT:
		    	adjTypeStr = "UNIT_DECREMENT";
		    	break;
		    case AdjustmentEvent.BLOCK_INCREMENT:
		    	adjTypeStr = "BLOCK_INCREMENT";
		    	break;
		    case AdjustmentEvent.BLOCK_DECREMENT:
		    	adjTypeStr = "BLOCK_DECREMENT";
		    	break;
		    case AdjustmentEvent.TRACK:
		    	adjTypeStr = "TRACK";
		    	break;
		    default:
		    	adjTypeStr = "unknown type";
		    }
		    eventMethodDetail = "adjType="+adjTypeStr
			    + ",value=" + e.getValue()
			    + ",isAdjusting="+e.getValueIsAdjusting();
        } break;
      
        // ItemEvent
        case ItemEvent.ITEM_STATE_CHANGED: {
    		enableEvent = enableItemEventStateChanged;
            typeStr = "ITEM_STATE_CHANGED";
            eventClassName = "ItemEvent";
			eventMethodName = "ItemEventStateChanged";
			ItemEvent e = (ItemEvent) event;
	        String stateStr;
	        switch(e.getStateChange()) {
	          case ItemEvent.SELECTED:
	              stateStr = "SELECTED";
	              break;
	          case ItemEvent.DESELECTED:
	              stateStr = "DESELECTED";
	              break;
	          default:
	              stateStr = "unknown type";
	        }
	        eventMethodDetail = "item="+e.getItem() + ",stateChange="+stateStr;
        } break;

        // TextEvent
        case TextEvent.TEXT_VALUE_CHANGED:
    		enableEvent = enableTextEventValueChanged;
            typeStr = "TEXT_VALUE_CHANGED";
            eventClassName = "TextEvent";
			eventMethodName = "TextValueChanged";
            break;
            
		// InputMethodEvent
		case InputMethodEvent.INPUT_METHOD_TEXT_CHANGED:
			enableEvent = enableInputMethodEventTextChanged;
			typeStr = "INPUT_METHOD_TEXT_CHANGED";
			eventClassName = "InputMethodEvent";
			eventMethodName = "TextChanged";
			break;
		case InputMethodEvent.CARET_POSITION_CHANGED:
			enableEvent = enableInputMethodEventCaretChanged;
			typeStr = "CARET_POSITION_CHANGED";
			eventClassName = "InputMethodEvent";
			eventMethodName = "CaretPosChanged";
			break;

		// PaintEvent ?? 
		
		// InvocationEvent, INVOCATION_EVENT_MASK 
        case InvocationEvent.INVOCATION_DEFAULT:
    		enableEvent = enableInvocationEventDefault;
	        typeStr = "INVOCATION_DEFAULT";
	        eventClassName = "InvocationEvent";
			eventMethodName = "Default";
	        break;
	        
		// HierarchyEvent, HIERARCHY_EVENT_MASK
  	  case HierarchyEvent.ANCESTOR_MOVED:
  		  enableEvent = enableHierarchyEventAncestorMoved;
  		  typeStr = "ANCESTOR_MOVED";
  		  eventClassName = "HierarchyEvent";
  		  eventMethodName = "Moved";
  		  // "("+changed+","+changedParent+")";
  		  break;
  	  case HierarchyEvent.ANCESTOR_RESIZED:
  		  enableEvent = enableHierarchyEventAncestorResized;
  		  typeStr = "ANCESTOR_RESIZED";
  		  eventClassName = "HierarchyEvent";
  		  eventMethodName = "Resized";
  		  // ("+changed+","+changedParent+")";
  		  break;
  	  case HierarchyEvent.HIERARCHY_CHANGED: {
  		  enableEvent = enableHierarchyEventChanged;
  		  typeStr = "HIERARCHY_CHANGED";
  		  eventClassName = "HierarchyEvent";
  		  eventMethodName = "Changed";
  	  } break;
	    
  	  // HIERARCHY_BOUNDS_EVENT_MASK ??
  	  
  	  // MouseWheelEvent, MOUSE_WHEEL_EVENT_MASK ... cf MouseEvent
  	  
  	  // WINDOW_STATE_EVENT_MASK ... cf WindowEvent
  	  
  	  // WINDOW_FOCUS_EVENT_MASK .. cf WindowEvent
  	  
  	  
  	  default:
			// TODO cases NOT_IMPLEMENTED YET ...
  		  	enableEvent = true;
			typeStr = "ID:" + event.getID();
			eventClassName = null;
			eventMethodName = null;
			break;
		}
		
		if (enableEvent) {
			RecordEventSummary eventInfo = new RecordEventSummary();
			eventInfo.setEventType("AWTSpy");
			eventInfo.setEventSubType(typeStr);
			eventInfo.setEventDate(new Date());
			eventInfo.setThreadName(Thread.currentThread().getName()); // should be the EDT ...
			eventInfo.setEventClassName(eventClassName);
			eventInfo.setEventMethodName(eventMethodName);
			eventInfo.setEventMethodDetail(eventMethodDetail);
			
			try {
				eventWriter.addEvent(eventInfo, eventData, null);
			} catch(Exception ex) {
				log.warn("should not occur.. ignore", ex);
			}
		}
	}

}
