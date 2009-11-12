package com.google.code.joto.eventrecorder.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventHandle;

/**
 * 
 */
public class FileRecordEventStore extends AbstractRecordEventStore {

//	private static Logger log = Logger.getLogger(FileRecordEventStore.class.getName());
	
	
	private File dir;
	private String fileName;
	
	private PrintWriter currEventHandleWriter;
	
	private RandomAccessFile currEventDataFile;
	private long currEventDataFileEndPosition;
	
	// keep all EventHandles in memory ??  (but not EventData in any case)
	private List<RecordEventHandle> eventHandleList = new ArrayList<RecordEventHandle>();
	
	private WeakHashMap<Integer,RecordEventData> eventsDataCache = new WeakHashMap<Integer,RecordEventData>();
	
	// ------------------------------------------------------------------------

	public FileRecordEventStore(File dir, String fileName) {
		this.dir = dir;
		this.fileName = fileName;

		open();
	}
	
	/** implements RecordEventStore */
	public void open() {
		File eventHandlesFile = new File(dir, fileName + "-events.txt");
		if (!eventHandlesFile.exists()) {
			try {
				eventHandlesFile.createNewFile();
			} catch(Exception ex) {
				throw new RuntimeException("Failed to create file " + eventHandlesFile, ex);
			}
		}
		
		File eventDataFile = new File(dir, fileName + "-data.ser");
		if (!eventDataFile.exists()) {
			try {
				eventDataFile.createNewFile();
			} catch(Exception ex) {
				throw new RuntimeException("Failed to create file " + eventDataFile, ex);
			}
		}
		
		try {
			currEventHandleWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream(eventHandlesFile)));
		} catch(IOException ex) {
			throw new RuntimeException("Failed to open " + eventHandlesFile, ex);
		}
		
		try {
			this.currEventDataFile = new RandomAccessFile(eventDataFile, "rw");
		} catch(Exception ex) {
			throw new RuntimeException("Failed to open file " + eventDataFile, ex);
		}
		this.currEventDataFileEndPosition = 0;
	}
	
	/** implements RecordEventStore */
	public void close() {
		if (currEventHandleWriter != null) {
			try {
				currEventHandleWriter.close();
			} catch(Exception ex) {
				// log.error("Failed to close file!", ex);
			}
			this.currEventHandleWriter = null;
		}
		
		if (currEventDataFile != null) {
			try {
				currEventDataFile.close();
			} catch(Exception ex) {
				// log.error("Failed to close file!", ex);
			}
			this.currEventDataFile = null;
		}
	}
	
	/** implements RecordEventStore */
	public synchronized void purgeCache() {
		eventsDataCache.clear();
	}
	
	// ------------------------------------------------------------------------
	
	/** implements RecordEventStore */
	public synchronized void addEvent(RecordEventHandle info, byte[] data) {
		try {
			long currFilePos = currEventDataFile.getFilePointer();
			RecordEventData eventData = createNewEventData(info, data);
			eventData.getEventHandle().setObjectDataFilePosition(currFilePos);
			
			doWriteEventData(eventData);
			
			eventsDataCache.put(eventData.getEventId(), eventData);
			
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/** implements RecordEventStore */
	public synchronized RecordEventData getEventData(RecordEventHandle eventHandle) {
		Integer eventId = eventHandle.getEventId();
		RecordEventData res = eventsDataCache.get(eventId);
		if (res == null) {
			res = doReadEventData(eventHandle);
			eventsDataCache.put(eventId, res);
		}
		return res;
	}

	/** implements RecordEventStore */
	public synchronized List<RecordEventHandle> getEvents() {
		List<RecordEventHandle> res = new ArrayList<RecordEventHandle>(eventHandleList);
		return res;
	}

	// ------------------------------------------------------------------------

	protected void doWriteEventData(RecordEventData eventData) {
		eventHandleList.add(eventData.getEventHandle());
		
		doLogEventHandle(eventData.getEventHandle());
				
		try {
			currEventDataFile.seek(currEventDataFileEndPosition);
			RAFOutputStream outputStream = new RAFOutputStream(currEventDataFile);

			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(eventData);
			
			currEventDataFileEndPosition = currEventDataFile.getFilePointer();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private final DateFormat displayDateFormat = new SimpleDateFormat("HH:mm:ss");
	
	/** utility helper, to log Event as text */
	private void doLogEventHandle(RecordEventHandle p) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(displayDateFormat.format(p.getEventDate()));
		sb.append(' ');
		sb.append(Integer.toString(p.getEventId()));
		sb.append(' ');
		sb.append(p.getEventType());
		sb.append(' ');
		sb.append(p.getEventSubType());
		sb.append(' ');
		sb.append(p.getEventMethodName());
		sb.append(' ');
		sb.append(p.getEventMethodDetail());
		sb.append(" @");
		sb.append(p.getObjectDataFilePosition());
		
		currEventHandleWriter.println(sb.toString());
	}

	protected RecordEventData doReadEventData(RecordEventHandle eventHandle) {
		RecordEventData res;
		long filePos = eventHandle.getObjectDataFilePosition();
		try {
			currEventDataFile.seek(filePos);
			InputStream inputStream = new RAFInputStream(currEventDataFile);
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			
			res = (RecordEventData) objectInputStream.readObject();
			
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * addapter RandomAccessFile -> InputStream   ?? not in jdk ??
	 */
	private static class RAFInputStream extends InputStream {
		
		private RandomAccessFile raf;
				
		public RAFInputStream(RandomAccessFile raf) {
			super();
			this.raf = raf;
		}

		@Override
		public int read() throws IOException {
			return raf.read();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return raf.read(b, off, len);
		}

		@Override
		public int read(byte[] b) throws IOException {
			return raf.read(b);
		}
		
	}

	/**
	 * addapter RandomAccessFile -> OutputStream   ?? not in jdk ??
	 */
	private static class RAFOutputStream extends OutputStream {
		
		private RandomAccessFile raf;
				
		public RAFOutputStream(RandomAccessFile raf) {
			super();
			this.raf = raf;
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			raf.write(b, off, len);
		}

		@Override
		public void write(byte[] b) throws IOException {
			raf.write(b);
		}

		@Override
		public void write(int b) throws IOException {
			raf.write(b);
		}
		
	}
	
}
