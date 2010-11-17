package uk.co.unclealex.music.encoding;

import java.io.File;
import java.util.Collection;

public interface FileFixingService {

	public void fixFlacFilenames(Collection<File> flacFiles);

	public File getFixedFlacFilename(String artist, String album, int trackNumber, String title);
}
