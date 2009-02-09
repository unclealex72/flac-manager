package uk.co.unclealex.music.base.writer;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

import uk.co.unclealex.music.base.service.titleformat.TitleFormatService;

public interface TrackWriterFactory {

	public TrackWriter createTrackWriter(TrackStream trackStream, TitleFormatService titleFormatService);
	public TrackWriter createTrackWriter(Map<TrackStream, TitleFormatService> titleFormatServicesByTrackStream);
	
	public TrackStream createFileTrackStream(File baseDir);
	public TrackStream createZipTrackStream(OutputStream out);
}
