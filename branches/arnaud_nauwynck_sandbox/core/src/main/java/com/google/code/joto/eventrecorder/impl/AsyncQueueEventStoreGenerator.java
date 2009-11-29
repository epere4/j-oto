package com.google.code.joto.eventrecorder.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventStoreCallback;
import com.google.code.joto.eventrecorder.RecordEventStoreGenerator;
import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * Asynchronous implementation for RecordEventStoreGenerator
 */
public class AsyncQueueEventStoreGenerator extends RecordEventStoreGenerator {

	private static class QueueEventData {
		RecordEventSummary event;
		Serializable objData;
		RecordEventStoreCallback callback;

		public QueueEventData(RecordEventSummary event, Serializable objData,
				RecordEventStoreCallback callback) {
			super();
			this.event = event;
			this.objData = objData;
			this.callback = callback;
		}
		
	}
	
	private static enum ThreadStatus {
		stopped,
		running,
		running_interrupting
	}
	
	private Object lock = new Object(); 

	private Queue<QueueEventData> queue = new LinkedList<QueueEventData>();
	
	private ThreadStatus currThreadStatus;

	
	//-------------------------------------------------------------------------

	public AsyncQueueEventStoreGenerator(RecordEventStore eventStore) {
		super(eventStore);
	}

	//-------------------------------------------------------------------------

	public void startQueue() {
		synchronized(lock) {
			switch(currThreadStatus) {
			case stopped: 
				new Thread(new Runnable() {
					public void run() {
						doRunThreadLoop();
					}
				}).start();
				break;
			case running: 
				// do nothing
				break;
			case running_interrupting: 
				currThreadStatus = ThreadStatus.running; // reset 
				break;
			}
		}
	}

	public void stopQueue() {
		synchronized(lock) {
			switch(currThreadStatus) {
			case stopped: 
				// do nothing
				break;
			case running: 
				currThreadStatus = ThreadStatus.running_interrupting;
				break;
			case running_interrupting: 
				// do nothing
				break;
			}
		}
	}

	@Override
	public void addEvent(RecordEventSummary event, Serializable objData,
			RecordEventStoreCallback callback) {
		if (!enableGenerator) {
			return;
		}
		
		QueueEventData queueObj = new QueueEventData(event, objData, callback); 
		synchronized(lock) {
			queue.add(queueObj);
		}
	}


	protected void doRunThreadLoop() {
		for(;;) {
			QueueEventData tmpToProcess = null; 
			synchronized(lock) {
				if (currThreadStatus == ThreadStatus.running_interrupting) {
					currThreadStatus = ThreadStatus.stopped;
					break;
				}
				if (!queue.isEmpty()) {
					tmpToProcess = queue.peek();
				} else {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						currThreadStatus = ThreadStatus.stopped;
						break;
					}
				}
			}
			
			RecordEventData stored = eventStore.addEvent(
					tmpToProcess.event, 
					tmpToProcess.objData); 
			
			if (tmpToProcess.callback != null) {
				tmpToProcess.callback.onStore(stored);
			}
		}
	}

	// override java.lang.Object 
	// -------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "AsyncQueueEventStoreGenerator[" 
			+ "currThreadStatus=" + currThreadStatus 
			+ ", queue length=" + queue.size()
			+ "]";
	}
	
}
