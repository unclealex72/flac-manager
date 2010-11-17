package uk.co.unclealex.music.encoding;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public interface RenamingService {

	public void merge(String albumTitle, Set<File> flacDirectories) throws IOException;
	
	public void rename(Set<File> flacFiles, String artist, String album, Integer trackNumber, String title) throws IOException;
}
