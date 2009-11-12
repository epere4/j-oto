package com.google.code.joto.eventrecorder;

import com.google.code.joto.eventrecorder.RecordEventChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventChange.TruncateRecordEventStoreEvent;

public interface RecordEventChangeVisitor {
	
	public void caseAddEvent(AddRecordEventStoreEvent p);
	public void caseTruncateEvent(TruncateRecordEventStoreEvent p);
	
}