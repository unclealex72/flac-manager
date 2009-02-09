package uk.co.unclealex.music.base.writer;

import java.io.IOException;

public interface WritingListener {

	public void initialise(int totalFiles);

	public void beforeFileWrites() throws IOException;
	
	public void registerFileWrite();

	public void registerFileWritten(String track, int length);

	public void afterFilesWritten() throws IOException;
	
	public void finish();

	public void finish(IOException exception);

	public void registerFileIgnore(String title);

}