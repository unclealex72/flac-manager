package uk.co.unclealex.music.covers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface ArtworkSearchingService {

	public List<URL> findArtwork(File flacFile) throws IOException;

}
