package uk.co.unclealex.flacconverter.encoded.writer;

import java.io.File;
import java.io.OutputStream;

public interface TrackWriterFactory {

	public TrackWriter createFileTrackWriter(File baseDir);
	public TrackWriter createZipTrackWriter(OutputStream out);
}
