package uk.co.unclealex.music.legacy.encoding;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

public interface RenamingService {

	public void merge(String albumTitle, Set<File> flacDirectories) throws IOException;
	
	public void rename(
			SortedSet<File> flacFiles, String artist, String album, Boolean compilation, Integer trackNumber, String title) throws IOException;
}
