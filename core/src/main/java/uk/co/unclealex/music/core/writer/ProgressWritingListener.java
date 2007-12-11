package uk.co.unclealex.music.core.writer;

import java.io.IOException;
import java.util.LinkedList;

import uk.co.unclealex.music.core.log.FormattableLogger;

public class ProgressWritingListener implements WritingListener {

	private static final FormattableLogger log = new FormattableLogger(ProgressWritingListener.class);
	
	private Progress i_progress;
	private IOException i_exception;
	private LinkedList<String> i_fileNamesWritten = new LinkedList<String>();
	private int i_totalFiles;
	private int i_filesWrittenCount;
	private long i_writeStartTime;
	private Thread i_thread;
	
	public void initialise(int totalFiles) {
		setProgress(Progress.PREPARING);
		setException(null);
		setFileNamesWritten(new LinkedList<String>());
		setTotalFiles(totalFiles);
		setFilesWrittenCount(0);
		setThread(Thread.currentThread());
	}
	
	@Override
	public void beforeFileWrites() {
	}
	
	public void registerFileWrite() {
		setProgress(Progress.WRITING);
		setWriteStartTime(System.currentTimeMillis());
	}
	
	@Override
	public void registerFileIgnore(String title) {
		setProgress(Progress.WRITING);
		getFileNamesWritten().add(title);
		i_filesWrittenCount++;
		log.info("Ignoring %s", title);
	}
	
	public void registerFileWritten(String fileName, int length) {
		getFileNamesWritten().add(fileName);
		i_filesWrittenCount++;
		double speed = length / ((System.currentTimeMillis() - getWriteStartTime()) * 1.024);
		log.info("Written %s in %.2fkb/s", fileName, speed);
	}
	
	@Override
	public void afterFilesWritten() {
		setProgress(Progress.FINALISING);
	}
	
	public void finish() {
		finish(null);
	}
	
	public void finish(IOException exception) {
		setProgress(Progress.FINISHED);
		setException(exception);
	}
	
	public void join() throws InterruptedException {
		getThread().join();
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

	public long getWriteStartTime() {
		return i_writeStartTime;
	}

	public void setWriteStartTime(long writeStartTime) {
		i_writeStartTime = writeStartTime;
	}

	public Progress getProgress() {
		return i_progress;
	}

	public void setProgress(Progress progress) {
		i_progress = progress;
	}
}
