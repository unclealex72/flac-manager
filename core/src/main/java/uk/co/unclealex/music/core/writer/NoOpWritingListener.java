package uk.co.unclealex.music.core.writer;

import java.io.IOException;

import uk.co.unclealex.music.base.writer.WritingListener;

public abstract class NoOpWritingListener implements WritingListener {

	@Override
	public void finish() {
	}

	@Override
	public void finish(IOException exception) {
	}

	@Override
	public void initialise(int totalFiles) {
	}

	@Override
	public void registerFileIgnore(String title) {
	}
	
	@Override
	public void registerFileWrite() {
	}

	@Override
	public void registerFileWritten(String track, int length) {
	}

}
