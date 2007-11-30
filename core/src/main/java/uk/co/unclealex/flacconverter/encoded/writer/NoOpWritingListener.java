package uk.co.unclealex.flacconverter.encoded.writer;

import java.io.IOException;

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
