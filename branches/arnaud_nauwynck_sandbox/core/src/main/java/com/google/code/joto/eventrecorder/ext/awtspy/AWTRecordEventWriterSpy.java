package com.google.code.joto.eventrecorder.ext.awtspy;

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

	protected RecordEventWriter eventWriter;

	protected boolean enable = false;

	protected long awtEventMask = 
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

	protected AWTEventListener innerAWTEventListener;

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
			if (enable) {
				uninstallAWTEventListener();
			}
			this.enable = p;
			if (enable) {
				installAWTEventListener();
			}
		}
	}
	
	public long getAwtEventMask() {
		return awtEventMask;
	}

	public void setAwtEventMask(long p) {
		if (p != awtEventMask) {
			if (enable) {
				uninstallAWTEventListener();
			}
			this.awtEventMask = p;
			if (enable) {
				installAWTEventListener();
			}
		}
	}

	public void installAWTEventListener() {
		log.info("add AWTEventListener for joto event writer");
		try {
			java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(
					innerAWTEventListener, awtEventMask);
		} catch (SecurityException ex) {
			log.error("Failed to add AWTEventListener ... ignore!", ex);
		}
	}

	public void uninstallAWTEventListener() {
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

		switch (event.getID()) {

		// ComponentEvent
		case ComponentEvent.COMPONENT_SHOWN:
			typeStr = "COMPONENT_SHOWN";
			eventClassName = "ComponentEvent";
			eventMethodName = "Shown";
			break;
		case ComponentEvent.COMPONENT_HIDDEN:
			typeStr = "COMPONENT_HIDDEN";
			eventClassName = "ComponentEvent";
			eventMethodName = "Hidden";
			break;
		case ComponentEvent.COMPONENT_MOVED:
			typeStr = "COMPONENT_MOVED";
			// b.x+","+b.y+" "+b.width+"x"+b.height;
			eventClassName = "ComponentEvent";
			eventMethodName = "Moved";
			break;
		case ComponentEvent.COMPONENT_RESIZED:
			typeStr = "COMPONENT_RESIZED";
			// b.x+","+b.y+" "+b.width+"x"+b.height+;
			eventClassName = "ComponentEvent";
			eventMethodName = "Resized";
			break;

		// ContainerEvent
        case ContainerEvent.COMPONENT_ADDED:
            typeStr = "COMPONENT_ADDED";
			eventClassName = "ContainerEvent";
			eventMethodName = "CompAdded";
            break;
        case ContainerEvent.COMPONENT_REMOVED:
            typeStr = "COMPONENT_REMOVED";
			eventClassName = "ContainerEvent";
			eventMethodName = "CompRemoved";
            break;

        // FocusEvent 
        case FocusEvent.FOCUS_GAINED:
            typeStr = "FOCUS_GAINED";
			eventClassName = "FocusEvent";
			eventMethodName = "FocusGained";
    		// eventMethodDetail = (event.temporary ? ",temporary" : ",permanent") + ",opposite=" + event.getOppositeComponent();
            break;
        case FocusEvent.FOCUS_LOST:
            typeStr = "FOCUS_LOST";
			eventClassName = "FocusEvent";
			eventMethodName = "FocusLost";
    		// eventMethodDetail = (event.temporary ? ",temporary" : ",permanent") + ",opposite=" + event.getOppositeComponent();
            break;
	
		// KeyEvent
		case KeyEvent.KEY_PRESSED:
			typeStr = "KEY_PRESSED";
			eventClassName = "KeyEvent";
			eventMethodName = "Pressed";
			break;
		case KeyEvent.KEY_RELEASED:
			typeStr = "KEY_RELEASED";
			eventClassName = "KeyEvent";
			eventMethodName = "Released";
			break;
		case KeyEvent.KEY_TYPED:
			typeStr = "KEY_TYPED";
			eventClassName = "KeyEvent";
			eventMethodName = "Typed";
			break;

		// MouseEvent 
		case MouseEvent.MOUSE_PRESSED:
			typeStr = "MOUSE_PRESSED";
			eventClassName = "MouseEvent";
			eventMethodName = "";
			break;
		case MouseEvent.MOUSE_RELEASED:
			typeStr = "MOUSE_RELEASED";
			eventClassName = "MouseEvent";
			eventMethodName = "";
			break;
		case MouseEvent.MOUSE_CLICKED:
			typeStr = "MOUSE_CLICKED";
			eventClassName = "MouseEvent";
			eventMethodName = "Clicked";
			break;
		case MouseEvent.MOUSE_MOVED:
			typeStr = "MOUSE_MOVED";
			eventClassName = "MouseEvent";
			eventMethodName = "Moved";
			break;
		case MouseEvent.MOUSE_DRAGGED:
			typeStr = "MOUSE_DRAGGED";
			eventClassName = "MouseEvent";
			eventMethodName = "Dragged";
			break;
		case MouseEvent.MOUSE_ENTERED:
			typeStr = "MOUSE_ENTERED";
			eventClassName = "MouseEvent";
			eventMethodName = "Entered";
			break;
		case MouseEvent.MOUSE_EXITED:
			typeStr = "MOUSE_EXITED";
			eventClassName = "MouseEvent";
			eventMethodName = "Exited";
			break;
		case MouseEvent.MOUSE_WHEEL: // cf also MouseWheelEvent
			typeStr = "MOUSE_WHEEL";
			eventClassName = "MouseEvent";
			eventMethodName = "Wheel";
			break;

		// WindowEvent
        case WindowEvent.WINDOW_OPENED:
            typeStr = "WINDOW_OPENED";
			eventClassName = "WindowEvent";
			eventMethodName = "Opened";
            break;
        case WindowEvent.WINDOW_CLOSING:
            typeStr = "WINDOW_CLOSING";
			eventClassName = "WindowEvent";
			eventMethodName = "Closing";
            break;
        case WindowEvent.WINDOW_CLOSED:
            typeStr = "WINDOW_CLOSED";
			eventClassName = "WindowEvent";
			eventMethodName = "Closed";
            break;
        case WindowEvent.WINDOW_ICONIFIED:
            typeStr = "WINDOW_ICONIFIED";
			eventClassName = "WindowEvent";
			eventMethodName = "Iconified";
            break;
        case WindowEvent.WINDOW_DEICONIFIED:
            typeStr = "WINDOW_DEICONIFIED";
			eventClassName = "WindowEvent";
			eventMethodName = "Deiconified";
            break;
        case WindowEvent.WINDOW_ACTIVATED:
            typeStr = "WINDOW_ACTIVATED";
			eventClassName = "WindowEvent";
			eventMethodName = "Activated";
            break;
        case WindowEvent.WINDOW_DEACTIVATED:
            typeStr = "WINDOW_DEACTIVATED";
			eventClassName = "WindowEvent";
			eventMethodName = "Deactivated";
            break;
        case WindowEvent.WINDOW_GAINED_FOCUS:
        	typeStr = "WINDOW_GAINED_FOCUS";
			eventClassName = "WindowEvent";
			eventMethodName = "GainedFocus";
        	break;
        case WindowEvent.WINDOW_LOST_FOCUS:
        	typeStr = "WINDOW_LOST_FOCUS";
			eventClassName = "WindowEvent";
			eventMethodName = "LostFocus";
        	break;
        case WindowEvent.WINDOW_STATE_CHANGED:
        	typeStr = "WINDOW_STATE_CHANGED";
			eventClassName = "WindowEvent";
			eventMethodName = "StateChanged";
        	break;

        // ActionEvent
        case ActionEvent.ACTION_PERFORMED:
            typeStr = "ACTION_PERFORMED";
			eventClassName = "ActionEvent";
			eventMethodName = "Performed";
            break;

        // AdjustmentEvent
        case AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED: {
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
            typeStr = "TEXT_VALUE_CHANGED";
            eventClassName = "TextEvent";
			eventMethodName = "TextValueChanged";
            break;
            
		// InputMethodEvent
		case InputMethodEvent.INPUT_METHOD_TEXT_CHANGED:
			typeStr = "INPUT_METHOD_TEXT_CHANGED";
			eventClassName = "InputMethodEvent";
			eventMethodName = "TextChanged";
			break;
		case InputMethodEvent.CARET_POSITION_CHANGED:
			typeStr = "CARET_POSITION_CHANGED";
			eventClassName = "InputMethodEvent";
			eventMethodName = "CaretPosChanged";
			break;

		// PaintEvent ?? 
		
		// InvocationEvent, INVOCATION_EVENT_MASK 
        case InvocationEvent.INVOCATION_DEFAULT:
	        typeStr = "INVOCATION_DEFAULT";
	        eventClassName = "InvocationEvent";
			eventMethodName = "Default";
	        break;
	        
		// HierarchyEvent, HIERARCHY_EVENT_MASK
  	  case HierarchyEvent.ANCESTOR_MOVED:
  		  typeStr = "ANCESTOR_MOVED";
  		  eventClassName = "HierarchyEvent";
  		  eventMethodName = "Moved";
  		  // "("+changed+","+changedParent+")";
  		  break;
  	  case HierarchyEvent.ANCESTOR_RESIZED:
  		  typeStr = "ANCESTOR_RESIZED";
  		  eventClassName = "HierarchyEvent";
  		  eventMethodName = "Resized";
  		  // ("+changed+","+changedParent+")";
  		  break;
  	  case HierarchyEvent.HIERARCHY_CHANGED: {
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
			typeStr = "ID:" + event.getID();
			eventClassName = null;
			eventMethodName = null;
			break;
		}
		
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
