package uk.co.unclealex.music.core.service.filesystem;

import java.util.Date;
import java.util.SortedSet;

import uk.co.unclealex.music.core.model.EncodedTrackBean;

public interface FileSystemService {

	public EncodedTrackBean findByPath(String path) throws PathNotFoundException;
	
	public boolean isDirectory(String path) throws PathNotFoundException;

	public boolean objectExists(String path);
	
	public Date getModificationDate(String path) throws PathNotFoundException;
	
	public SortedSet<String> getChildren(String directory) throws PathNotFoundException;

	public Long getLength(String path) throws PathNotFoundException;
}
