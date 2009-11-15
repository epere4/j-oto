package com.google.code.joto.eventrecorder.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.io.ByteArrayOutputStream2;

/**
 * 
 */
public class FileRecordEventStore extends AbstractRecordEventStore {

	private static Logger log = Logger.getLogger(FileRecordEventStore.class.getName());
	
	private File eventDataFile;
	
	
	private OutputStream eventDataFileAppender;
	private RandomAccessFile eventDataRandomAccessFile;
	private Object eventDataRandomAccessFileLock = new Object();
	private long lastFilePosition;
	private int lastFlushedEventId;
	
	private ByteArrayOutputStream2 tmpBuffer = new ByteArrayOutputStream2();
	
	private EventSummaryCompressionContext eventSummaryCompressionContext = new EventSummaryCompressionContext();
	
	// TODO useless / externalize in wrapper class CacheRecordEventStore
//	private WeakReference<IntList> cacheEventFilePositionArray = new WeakReference(new IntList());
	private WeakHashMap<Integer,byte[]> cacheEventObjectDataById = new WeakHashMap<Integer,byte[]>();
	
	// ------------------------------------------------------------------------

	public FileRecordEventStore(File eventDataFile) {
		this.eventDataFile = eventDataFile;

		// to call next... open();
	}
	
	// -------------------------------------------------------------------------
	
	/** implements RecordEventStore */
	public void open(boolean appendOtherwiseReadonly) {
		if (!eventDataFile.exists()) {
			if (appendOtherwiseReadonly) {
				try {
					eventDataFile.createNewFile();
				} catch(Exception ex) {
					throw new RuntimeException("Failed to create eventDataFile " + eventDataFile, ex);
				}
			} else {
				// eventDataFiles does not exist, but open as readonly => error!
				throw new RuntimeException("eventDataFile not found " + eventDataFile + ", can not open RecordEventStore in readonly");
			}
		} else {
			if (appendOtherwiseReadonly) {
				// re-opening eventDataFile for writing? => erase (delete+create) eventDataFile first
				try {
					eventDataFile.delete();
					eventDataFile.createNewFile();
				} catch(Exception ex) {
					throw new RuntimeException("Failed to erase eventDataFile " + eventDataFile, ex);
				}
			}
		}
				
		this.readonly = !appendOtherwiseReadonly;
		try {
			String mode = (readonly)? "r" : "rw";
			this.eventDataRandomAccessFile = new RandomAccessFile(eventDataFile, mode);
			
			if (!readonly) {
				FileOutputStream fileOut = new FileOutputStream(eventDataFile);
				this.eventDataFileAppender = new BufferedOutputStream(fileOut, 8*8192);
			}
			
		} catch(Exception ex) {
			throw new RuntimeException("Failed to open eventDataFile " + eventDataFile, ex);
		}
		this.lastFilePosition = 0;
		this.lastFlushedEventId = 1;
	}
	
	/** implements RecordEventStore */
	public void close() {
		if (eventDataFileAppender != null) {
			try { 
				flush(); 
			} catch(Exception ex) {
				log.error("Failed to flush eventDataFile!", ex);
			}
			try {
				eventDataFileAppender.close();
			} catch(Exception ex) {
				log.error("Failed to close eventDataFile!", ex);
			}
			this.eventDataFileAppender = null;
		}

		if (eventDataRandomAccessFile != null) {
			try {
				eventDataRandomAccessFile.close();
			} catch(Exception ex) {
				log.error("Failed to close eventDataFile!", ex);
			}
			this.eventDataRandomAccessFile = null;
		}
	}

	/** implements RecordEventStore */
	public void flush() {
		if (eventDataFileAppender != null) {
			try {
				eventDataFileAppender.flush();
				// eventDataRandomAccessFile.getChannel().force(true);
				lastFlushedEventId = getLastEventId();
			} catch(IOException ex) {
				throw new RuntimeException("failed to flush file " + eventDataFile, ex);
			}
		}
	}

	/** purge for GC */
	public synchronized void purgeCache() {
		cacheEventObjectDataById.clear();
	}

	/** implements RecordEventStore */
	@Override
	public synchronized List<RecordEventSummary> getEvents(int fromEventId, int toEventId) {
		int eventIndex = fromEventId - getFirstEventId();
		if (eventIndex < 0) {
			throw new RuntimeException("event already purged");
		}
		int lastEventId = getLastEventId();
		if (toEventId == -1) {
			toEventId = lastEventId;
		} else if (toEventId < fromEventId) {
			throw new IllegalArgumentException("invalid toEventId:" + toEventId);
		} else if (toEventId > lastEventId) {
			throw new IllegalArgumentException("invalid toEventId:" + toEventId);
		}
		List<RecordEventSummary> res = new ArrayList<RecordEventSummary>(toEventId - fromEventId);
		
		int currEventPosition = 0;
		int currEventId = getFirstEventId();
//		IntList filePosArray = cacheEventFilePositionArray.get();
//		if (filePosArray != null) {
//			currEventPosition = filePosArray.getAt(eventIndex);
//			currEventId = fromEventId;
//		} else {
//			// need to scan from before..
//			filePosArray = new IntList(); // restore weak reference
//			cacheEventFilePositionArray = new WeakReference(filePosArray);
//			currEventPosition = 0;
//			currEventId = getFirstEventId();
//		}

		try {
			synchronized (eventDataRandomAccessFileLock) {
				// scan/skip currEventId -> until fromEventId
				if (toEventId > lastFlushedEventId) {
					flush();
				}
				eventDataRandomAccessFile.seek(currEventPosition);
				for(; currEventId < fromEventId; currEventId++) {
					int eventTotalSize = eventDataRandomAccessFile.readInt();
					currEventPosition += eventTotalSize;
					eventDataRandomAccessFile.seek(currEventPosition);
				}
				// reached fromEventId ... now read until toEventId				
				for(; currEventId < toEventId; currEventId++) {
					RecordEventData eventData = doReadEventData(currEventId, currEventPosition,
							true, null, false);
					res.add(eventData.getEventSummary());
				}
			}
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}


	/** implements RecordEventStore */
	@Override
	public void purgeEvents(int toEventId) {
		// not supported on file... do nothing!
	}

	
	// ------------------------------------------------------------------------
	
	/** implements RecordEventStore */
	public synchronized RecordEventData doAddEvent(RecordEventSummary info, byte[] objectDataBytes) {
		// prepare data to write in tmp buffer..
		// format: "<totalEventSize><eventSummarySize><encodedEventSummary><objectDataBytes>"
		tmpBuffer.reset();
		DataOutputStream tmpBufferDataOut = new DataOutputStream(tmpBuffer);
		int eventTotalSize;
		try {
			// "skip" 4 bytes for global size (header + encoded eventSummary + event data)
			// "skip" 4 bytes for size of encoded eventSummary
			tmpBufferDataOut.writeInt(0);
			tmpBufferDataOut.writeInt(0);
			eventSummaryCompressionContext.encode(info, tmpBufferDataOut);
			int eventSummarySize = tmpBuffer.getCount() - 8; 
			tmpBuffer.write(objectDataBytes);
			eventTotalSize = tmpBuffer.getCount(); 

			// now tmp re-wind to write size of encoded eventSummary...
			tmpBuffer.setCount(0); // tmp re-wind
			tmpBufferDataOut.writeInt(eventTotalSize);
			tmpBufferDataOut.writeInt(eventSummarySize);
			tmpBuffer.setCount(eventTotalSize); // restore
		} catch(IOException ex) {
			throw new RuntimeException("should not occur on buffer!", ex);
		}
		
		// byte[] tmpOutBufferArray = tmpOutBuffer.toByteArray(); ... local copy
		byte[] eventBufferBytes = tmpBuffer.getBuffer();
		
		// do lock(? ..already done)+write
		RecordEventData eventData = createNewEventData(info, objectDataBytes);
		
		doWriteEventData(eventData, eventBufferBytes, eventTotalSize);
		
		cacheEventObjectDataById.put(eventData.getEventId(), eventData.getObjectDataBytes());
		
		return eventData;
	}

	/** implements RecordEventStore */
	public synchronized RecordEventData getEventData(RecordEventSummary eventSummary) {
		Integer eventId = eventSummary.getEventId();
		byte[] objDataBytes = cacheEventObjectDataById.get(eventId);
		if (objDataBytes == null) {
			RecordEventData tmp = doReadEventData(
					eventSummary.getEventId(),
					eventSummary.getInternalEventStoreDataAddress(),
					false, eventSummary, 
					true);
			objDataBytes = tmp.getObjectDataBytes();
			cacheEventObjectDataById.put(eventId, objDataBytes);
		}
		return new RecordEventData(eventSummary, objDataBytes);
	}

	// ------------------------------------------------------------------------

	protected void doWriteEventData(RecordEventData eventData, byte[] preparedBytes, int preparedBytesLen) {
		try {
			synchronized(eventDataRandomAccessFileLock) {
				eventData.getEventSummary().setInternalEventStoreDataAddress(lastFilePosition);

//				long tmppos = eventDataRandomAccessFile.getFilePointer();
//				if (tmppos != lastFilePosition) {
//					eventDataRandomAccessFile.seek(lastFilePosition);
//				}
//				eventDataRandomAccessFile.write(preparedBytes, 0, preparedBytesLen);
//				// lastFilePosition = eventDataRandomAccessFile.getFilePointer()

				this.eventDataFileAppender.write(preparedBytes, 0, preparedBytesLen);
				
				lastFilePosition += preparedBytesLen;
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	

	protected RecordEventData doReadEventData(
			int eventId,
			long filePosition,
			boolean readRecordEventSummary,
			RecordEventSummary recordEventSummary, //... already read, reread??
			boolean readEventData) {
		RecordEventData res;
		synchronized(eventDataRandomAccessFileLock) {
			try {
				if (eventId > lastFlushedEventId) {
					flush();
				}
				eventDataRandomAccessFile.seek(filePosition);

				int eventTotalSize = eventDataRandomAccessFile.readInt();
				int eventSummarySize = eventDataRandomAccessFile.readInt();
				if (readRecordEventSummary && recordEventSummary == null) {
					tmpBuffer.reset();
					tmpBuffer.ensureCapacity(eventSummarySize);
					byte[] buffer = tmpBuffer.getBuffer();
					eventDataRandomAccessFile.read(buffer, 0, eventSummarySize);
					
					DataInputStream din = new DataInputStream(new ByteArrayInputStream(buffer, 0, eventSummarySize));
					recordEventSummary = eventSummaryCompressionContext.decode(eventId, din);
					// recordEventSummary.setEventId(eventId);// not possible... final => ctor copy!
					recordEventSummary = new RecordEventSummary(eventId, recordEventSummary);
				} else {
					// reread/seek/skip?
					eventDataRandomAccessFile.skipBytes(eventSummarySize);
				}
				// read data bytes
				byte[] eventObjectData = null;
				int eventDataSize = eventTotalSize - eventSummarySize - 8;
				if (readEventData) {
					eventObjectData = new byte[eventDataSize];
					eventDataRandomAccessFile.read(eventObjectData);
				} else {
					// seek/skip
					eventDataRandomAccessFile.skipBytes(eventDataSize);
				}
				
				res = new RecordEventData(recordEventSummary, eventObjectData);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return res;
	}
	
}
