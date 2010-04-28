package uk.co.unclealex.music.encoding;

import java.io.File;
import java.util.SortedSet;

public interface ArtworkUpdatingService {

	public boolean updateArtwork(SortedSet<File> flacFiles, SortedSet<File> possibleImageFiles);

	public void updateEncodedArtwork(Encoding encoding, File flacFile, File encodedDestination);

}
