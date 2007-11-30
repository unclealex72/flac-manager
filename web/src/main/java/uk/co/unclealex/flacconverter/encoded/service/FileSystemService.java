package uk.co.unclealex.flacconverter.encoded.service;

import java.io.InputStream;
import java.util.Date;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;

public interface FileSystemService {

	public boolean exists(String path);
	
	public boolean isDirectory(String path);
	
	public String[] getChildPaths(String directoryPath);
	
	public EncodedTrackBean getEncodedTrackBean(String path);

	public Date getCreationDate(String path);

	public InputStream getResourceAsStream(String path);
}
