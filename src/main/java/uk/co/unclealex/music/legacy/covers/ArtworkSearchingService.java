package uk.co.unclealex.music.legacy.covers;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ArtworkSearchingService {

	public List<String> findArtwork(File flacFile) throws IOException;

	public List<String> findArtwork(String artist, String album) throws IOException;

}
