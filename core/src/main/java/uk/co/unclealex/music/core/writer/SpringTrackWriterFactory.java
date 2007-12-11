package uk.co.unclealex.music.core.writer;

import static java.util.zip.ZipOutputStream.STORED;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import uk.co.unclealex.music.core.service.titleformat.TitleFormatService;

public class SpringTrackWriterFactory implements TrackWriterFactory, ApplicationContextAware {

	private ApplicationContext i_applicationContext;
	private String i_zipTrackStreamId = "zipTrackStream";
	private String i_fileTrackStreamId = "fileTrackStream";
	private String i_trackWriterId = "trackWriter";
	
	@Override
	public TrackWriter createTrackWriter(TrackStream trackStream, TitleFormatService titleFormatService) {
		Map<TrackStream, TitleFormatService> map = new HashMap<TrackStream, TitleFormatService>();
		map.put(trackStream, titleFormatService);
		return createTrackWriter(map);
	}
	
	@Override
	public TrackWriter createTrackWriter(Map<TrackStream, TitleFormatService> titleFormatsByTrackStream) {
		TrackWriter trackWriter = 
			(TrackWriter) getApplicationContext().getBean(getTrackWriterId(), TrackWriter.class);
		trackWriter.initialise(titleFormatsByTrackStream);
		return trackWriter;
	}

	@Override
	public TrackStream createFileTrackStream(File baseDir) {
		FileTrackStream fileTrackStream = 
			(FileTrackStream) getApplicationContext().getBean(getFileTrackStreamId(), FileTrackStream.class);
		fileTrackStream.setRootDirectory(baseDir);
		return fileTrackStream;
	}
	
	@Override
	public TrackStream createZipTrackStream(OutputStream out) {
		ZipTrackStream zipTrackStream =
			(ZipTrackStream) getApplicationContext().getBean(getZipTrackStreamId(), ZipTrackStream.class);
		ZipOutputStream zipOutputStream = new ZipOutputStream(out);
		zipOutputStream.setMethod(STORED);
		zipTrackStream.setZipOutputStream(zipOutputStream);
		return zipTrackStream;
	}
	
	public ApplicationContext getApplicationContext() {
		return i_applicationContext;
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		i_applicationContext = applicationContext;
	}
	public String getZipTrackStreamId() {
		return i_zipTrackStreamId;
	}
	public void setZipTrackStreamId(String zipTrackStreamId) {
		i_zipTrackStreamId = zipTrackStreamId;
	}
	public String getFileTrackStreamId() {
		return i_fileTrackStreamId;
	}
	public void setFileTrackStreamId(String fileTrackStreamId) {
		i_fileTrackStreamId = fileTrackStreamId;
	}

	public String getTrackWriterId() {
		return i_trackWriterId;
	}

	public void setTrackWriterId(String trackWriterId) {
		i_trackWriterId = trackWriterId;
	}
}
