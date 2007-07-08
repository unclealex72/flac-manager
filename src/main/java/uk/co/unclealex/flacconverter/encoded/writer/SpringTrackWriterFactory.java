package uk.co.unclealex.flacconverter.encoded.writer;

import java.io.File;
import java.util.zip.ZipOutputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringTrackWriterFactory implements TrackWriterFactory, ApplicationContextAware {

	private ApplicationContext i_applicationContext;
	private String i_zipTrackWriterId = "zipTrackWriter";
	private String i_fileTrackWriterId = "fileTrackWriter";
	
	@Override
	public TrackWriter createFileTrackWriter(File baseDir) {
		FileTrackWriter fileTrackWriter = 
			(FileTrackWriter) getApplicationContext().getBean(getFileTrackWriterId(), FileTrackWriter.class);
		fileTrackWriter.setRootDirectory(baseDir);
		return fileTrackWriter;
	}
	
	@Override
	public TrackWriter createZipTrackWriter(ZipOutputStream zipOutputStream) {
		ZipTrackWriter zipTrackWriter =
			(ZipTrackWriter) getApplicationContext().getBean(getZipTrackWriterId(), ZipTrackWriter.class);
		zipTrackWriter.setZipOutputStream(zipOutputStream);
		return zipTrackWriter;
	}
	
	public ApplicationContext getApplicationContext() {
		return i_applicationContext;
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		i_applicationContext = applicationContext;
	}
	public String getZipTrackWriterId() {
		return i_zipTrackWriterId;
	}
	public void setZipTrackWriterId(String zipTrackWriterId) {
		i_zipTrackWriterId = zipTrackWriterId;
	}
	public String getFileTrackWriterId() {
		return i_fileTrackWriterId;
	}
	public void setFileTrackWriterId(String fileTrackWriterId) {
		i_fileTrackWriterId = fileTrackWriterId;
	}
}
