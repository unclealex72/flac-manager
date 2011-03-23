package uk.co.unclealex.music.encoding;

import java.io.File;

public interface FileFixingService {

	public void fixFlacFilenames(Iterable<File> flacFiles);

	public File getFixedFlacFilename(String artist, String album, boolean compilation, int trackNumber, String title);
}
