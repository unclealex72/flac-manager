package uk.co.unclealex.flacconverter.encoded.writer;

import java.io.File;
import java.util.zip.ZipOutputStream;

public interface TrackWriterFactory {

	public TrackWriter createFileTrackWriter(File baseDir);
	public TrackWriter createZipTrackWriter(ZipOutputStream zipOutputStream);
}
