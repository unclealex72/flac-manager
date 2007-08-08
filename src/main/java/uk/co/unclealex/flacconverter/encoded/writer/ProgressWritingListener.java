package uk.co.unclealex.flacconverter.encoded.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

import org.apache.log4j.Logger;

public class WritingListener implements Serializable {

	private static final Logger log = Logger.getLogger(WritingListener.class);
	
	private boolean i_finished;
	private IOException i_exception;
	private LinkedList<String> i_fileNamesWritten = new LinkedList<String>();
	private int i_totalFiles;
	private int i_filesWrittenCount;
	private Thread i_thread;
	
	public void initialise(int totalFiles) {
		setFinished(false);
		setException(null);
		setFileNamesWritten(new LinkedList<String>());
		setTotalFiles(totalFiles);
		setFilesWrittenCount(0);
		setThread(Thread.currentThread());
	}
	
	public void registerFileWrite(String fileName) {
		getFileNamesWritten().add(fileName);
		i_filesWrittenCount++;
		log.info("Written " + fileName);
	}
	
	public void finish() {
		finish(null);
	}
	
	public void finish(IOException exception) {
		setFinished(true);
		setException(exception);
	}
	
	public void join() throws InterruptedException {
		getThread().join();
	}
	
	public boolean isFinished() {
		return i_finished;
	}
	public void setFinished(boolean finished) {
		i_finished = finished;
	}
	public IOException getException() {
		return i_exception;
	}
	public void setException(IOException exception) {
		i_exception = exception;
	}
	
	public LinkedList<String> getFileNamesWritten() {
		return i_fileNamesWritten;
	}
	
	protected void setFileNamesWritten(LinkedList<String> fileNamesWritten) {
		i_fileNamesWritten = fileNamesWritten;
	}
	public int getTotalFiles() {
		return i_totalFiles;
	}
	public void setTotalFiles(int totalFiles) {
		i_totalFiles = totalFiles;
	}
	public int getFilesWrittenCount() {
		return i_filesWrittenCount;
	}
	public void setFilesWrittenCount(int filesWrittenCount) {
		i_filesWrittenCount = filesWrittenCount;
	}

	public Thread getThread() {
		return i_thread;
	}

	protected void setThread(Thread thread) {
		i_thread = thread;
	}

}
