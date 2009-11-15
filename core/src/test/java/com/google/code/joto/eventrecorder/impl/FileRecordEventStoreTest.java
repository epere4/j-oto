package com.google.code.joto.eventrecorder.impl;

import java.io.File;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.testobj.TestObjFactory;

/**
 *
 */
public class FileRecordEventStoreTest extends TestCase {

	private File targetTestDir;
	
	public FileRecordEventStoreTest(String name) {
		super(name);
	}

	public void setUp() {
		targetTestDir = new File("target/test");
		if (!targetTestDir.exists()) {
			targetTestDir.mkdirs();
		}
	}
	
	public void testWrite1() {
		File file = new File(targetTestDir, "FileRecordEventStoreTest1.tmp");
		if (file.exists()) {
			file.delete();
		}
		FileRecordEventStore eventStore = new FileRecordEventStore(file);
		try {
			eventStore.open(true);

			generateEvent(eventStore, 0);

			eventStore.close();
		} catch(Exception ex) {
			eventStore.close();
			throw new RuntimeException(ex);
		}

		eventStore.open(false);
		checkGetEvents(eventStore, 1, 2);

	}
	
	public void testReadWriteMultiple() {
		File file = new File(targetTestDir, "FileRecordEventStoreTest.tmp");
		if (file.exists()) {
			file.delete();
		}
		try {
			FileRecordEventStore eventStore = new FileRecordEventStore(file);
			eventStore.open(true);

			int count = 0;
			RecordEventData evt1 = generateEvent(eventStore, 0);
			count++;
			assertNotNull(evt1);
			assertEquals(1, evt1.getEventId());
			assertEquals(1, eventStore.getFirstEventId());
			assertEquals(count+1, eventStore.getLastEventId());
			assertEquals(count, eventStore.getEventsCount());
			
			RecordEventData evt2 = generateEvent(eventStore, 0);
			count++;
			assertNotNull(evt2);
			assertEquals(2, evt2.getEventId());
			assertEquals(1, eventStore.getFirstEventId());
			assertEquals(count+1, eventStore.getLastEventId());
			assertEquals(count, eventStore.getEventsCount());
			
			generateEvents(eventStore, 1000, 0);
			count += 1000;
			assertEquals(count+1, eventStore.getLastEventId());
			assertEquals(count, eventStore.getEventsCount());
			
			
			assertEquals(1, eventStore.getFirstEventId());
			assertEquals(1003, eventStore.getLastEventId());
			assertEquals(1002, eventStore.getEventsCount());
			
			// test re-reading past events

			List<RecordEventSummary> events = checkGetEvents(eventStore, 1, count+1);
			// equivalent to ... checkGetEvents(eventStore, 0, -1);
						
			
			checkGetEvents(eventStore, 5, 10);

			checkGetEventDataList(eventStore, events);

			
			// alternate writing and re-reading
			Random rand = new Random(0);
			for (int i = 1; i < 5; i++) {
				int writeCount = i*2;
				generateEvents(eventStore, writeCount, 0);
				count += writeCount;
				assertEquals(count+1, eventStore.getLastEventId());
				assertEquals(count, eventStore.getEventsCount());
				
				int readFromEventId = 1 + rand.nextInt(count);
				int readToEventId = readFromEventId + rand.nextInt(count - 1 - readFromEventId);
				checkGetEvents(eventStore, readFromEventId, readToEventId);
				
			}
				
			// close and reopen in readonly
			eventStore.close();
			eventStore.open(false);
			
			// re-read
			
			List<RecordEventSummary> events2 = checkGetEvents(eventStore, 1, count);
			checkGetEvents(eventStore, 5, 10);

			checkGetEventDataList(eventStore, events2);
			
			// finish
			eventStore.close();
			
		} finally {
			// tearDown
			if (file.exists()) {
				file.delete();
			}
		}
	}

	private List<RecordEventSummary> checkGetEvents(RecordEventStore eventStore,
			int readFromEventId, int readToEventId) {
		if (readFromEventId == 0) {
			readFromEventId = 1;
		}
		if (readToEventId == -1) {
			readToEventId = eventStore.getLastEventId();
		}
		int readCount = readToEventId - readFromEventId; 
		List<RecordEventSummary> res = eventStore.getEvents(readFromEventId, readToEventId);
		assertNotNull(res);
		assertEquals(readCount, res.size());
		assertEquals(readFromEventId, res.get(0).getEventId());
		if (readCount > 1) {
			assertEquals(readFromEventId+1, res.get(1).getEventId());
		}
		if (readCount > 2) {
			assertEquals(readFromEventId+2, res.get(2).getEventId());
			assertEquals(readToEventId-3, res.get(readCount-3).getEventId());
		}
		if (readCount > 1) {
			assertEquals(readToEventId-2, res.get(readCount-2).getEventId());
		}
		assertEquals(readToEventId-1, res.get(readCount-1).getEventId());
		return res;
	}


	private void checkGetEventData(RecordEventStore eventStore, RecordEventSummary eventSummary) {
		RecordEventData eventData = eventStore.getEventData(eventSummary);
		byte[] objDataBytes = eventData.getObjectDataBytes();
		assertNotNull(objDataBytes);
		Object objDataCopy = eventData.getObjectDataCopy();
		assertNotNull(objDataCopy);
	}

	private void checkGetEventDataList(RecordEventStore eventStore, List<RecordEventSummary> events) {
		for (RecordEventSummary e : events) {
			checkGetEventData(eventStore, e);
		}
		// toadd: also test in random order?
	}

	private void generateEvents(RecordEventStore eventStore, int count, int randSeed) {
		for (int i = 0; i < count; i++) {
			generateEvent(eventStore, randSeed++);
		}
	}
	
	private RecordEventData generateEvent(RecordEventStore eventStore, int randSeed) {
		RecordEventSummary e = RecordEventSummary.snewDefault(
					"testEventType" + (randSeed%5), 
					"testEventSubType" + (randSeed%10), 
					"testMeth" + (randSeed%50));
		byte[] objDataBytes = RecordEventData.serializableToByteArray(
			TestObjFactory.createAnySerializableBean(randSeed));
		RecordEventData res = eventStore.addEvent(e, objDataBytes);
		return res;
	}


	private void generateEvents_SimpleIntFieldA(RecordEventStore eventStore, int count, int randSeed) {
		for (int i = 0; i < count; i++) {
			generateEvent_SimpleIntFieldA(eventStore, randSeed++);
		}
	}

	private RecordEventData generateEvent_SimpleIntFieldA(RecordEventStore eventStore, int randSeed) {
		RecordEventSummary e = RecordEventSummary.snewDefault(
					"testEventType" + (randSeed%5), 
					"testEventSubType" + (randSeed%10), 
					"testMeth" + (randSeed%50));
		byte[] objDataBytes = RecordEventData.serializableToByteArray(
			TestObjFactory.createSimpleIntFieldA());
		RecordEventData res = eventStore.addEvent(e, objDataBytes);
		return res;
	}

	public void testBenchmarkWriteSimple() {
		boolean deleteFile = true; // use false for debuggin(?): showing file content
		// 
		System.out.println("benchmark FileRecordEventStore ... First result is not significative because hotspot is lazy...");
		doTestBenchmarkWriteSimple("FileRecordEventStoreTest-Bench500x500.tmp", 50, 50, deleteFile); // => 
		System.out.println("now benchmark with different repeatCount x size");
		
		doTestBenchmarkWriteSimple("FileRecordEventStoreTest-Bench1000x10.tmp", 1000,   10, deleteFile);
		doTestBenchmarkWriteSimple("FileRecordEventStoreTest-Bench100x100.tmp",  100,  100, deleteFile);
		doTestBenchmarkWriteSimple("FileRecordEventStoreTest-Bench10x1000.tmp",   10, 1000, deleteFile); 
		
//		doTestBenchmarkWriteSimple("FileRecordEventStoreTest-Bench10x10000.tmp",   10, 10000); // => 11Mo 
//		doTestBenchmarkWriteSimple("FileRecordEventStoreTest-Bench5x500000.tmp",  5, 500000);

		System.out.println("benchmark FileRecordEventStore finished");
	}
	
	protected void doTestBenchmarkWriteSimple(String fileName, final int repeatCount, final int writeCount, boolean deleteFile) {
		System.gc();
		System.gc();
		File file = new File(targetTestDir, fileName);
		if (file.exists()) {
			file.delete();
		}
		FileRecordEventStore eventStore = new FileRecordEventStore(file);
		try {
			eventStore.open(true);
			
			long totalAddNanos = 0;
			long totalFlushNanos = 0;
			for (int i = 0; i < repeatCount; i++) {
				long nanos1 = System.nanoTime();
				generateEvents_SimpleIntFieldA(eventStore, writeCount, 0);
				long nanos2 = System.nanoTime();
				eventStore.flush();
				long nanos3 = System.nanoTime();

				totalAddNanos += (nanos2 - nanos1); 
				totalFlushNanos += (nanos3 - nanos2);
//				int millis10 = (int) (10*nanos / 1000000);
//				System.out.println("write+flush " + writeCount + " events with simple obj, " 
//						+ "took:" + (millis10*0.1) + " ms/" + writeCount
//						+ ", " + (millis10*0.1/writeCount) + " ms/u"
//						);
			}
			System.out.println("bench FileRecordEventStore: repeat " + repeatCount + " x write+flush " + writeCount + " events with simple obj\n"
					+ " total time: " + formatNanosTotalTime(totalAddNanos + totalFlushNanos, repeatCount, writeCount) + "\n"
					+ " time for addEvent(): " + formatNanosTotalTime(totalAddNanos, repeatCount, writeCount) + "\n"
					+ " time for flush(): " +  formatNanosTotalTime(totalFlushNanos, repeatCount, writeCount) + "\n"
					);
			
			// finish
			eventStore.close();
		} catch(Exception ex) {
			eventStore.close();
			throw new RuntimeException(ex);
		} finally {
			if (deleteFile) {
				file.delete();
			}
		}
		
	}

	private String formatNanosTotalTime(long totalNanos, int repeatCount, int size) {
		int totalMillis = (int) (totalNanos / 1000000); 
		String res = totalMillis + " ms" 
			+ ", " + (totalMillis/repeatCount) + " ms/" + size  + " in avg"
			+ ", " + ((double)totalMillis/repeatCount/size) + " ms/u in avg";
		return res;
	}
	
}
