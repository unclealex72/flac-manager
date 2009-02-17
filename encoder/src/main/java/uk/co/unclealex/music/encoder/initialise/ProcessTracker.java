package uk.co.unclealex.music.encoder.initialise;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

public class ProcessTracker {

	private static final Logger log = Logger.getLogger(ProcessTracker.class);
	
	private int i_size;
	private long i_startTime = System.currentTimeMillis();
	private int i_processed = 0;
	private long i_totalMillisecondsTaken = 0;
	
	public ProcessTracker(int size) {
		i_size = size;
	}

	public void ignore(File file) {
		setProcessed(getProcessed() + 1);
		log.info("Ignoring " + file);
	}

	public void imported(File file, String title, Date start, Date end) {
		long timeTaken = end.getTime() - start.getTime();
		log.info("Imported file " + file + " as " + title + " in " + timeTaken + "ms");
		setTotalMillisecondsTaken(getTotalMillisecondsTaken() + timeTaken);
		setProcessed(getProcessed() + 1);
		long finishTime = getStartTime() + (getTotalMillisecondsTaken() * (long) getSize()) / (long) getProcessed();
		log.info("Expected finishing time is " + new Date(finishTime));
		if (getProcessed() % 40 == 0) {
			System.gc();
		}
	}

	public int getSize() {
		return i_size;
	}

	protected int getProcessed() {
		return i_processed;
	}

	protected void setProcessed(int processed) {
		i_processed = processed;
	}

	protected long getTotalMillisecondsTaken() {
		return i_totalMillisecondsTaken;
	}

	protected void setTotalMillisecondsTaken(long totalMillisecondsTaken) {
		i_totalMillisecondsTaken = totalMillisecondsTaken;
	}

	public long getStartTime() {
		return i_startTime;
	}

}
