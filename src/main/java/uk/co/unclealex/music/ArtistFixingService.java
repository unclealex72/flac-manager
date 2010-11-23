package uk.co.unclealex.music;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;

public interface ArtistFixingService {

	public SortedSet<String> listArtists(File cacheFile) throws IOException;
	
	public void fixArtists(Map<String, String> newArtistNamesByOriginalArtistName, File cacheFile) throws IOException;
}
