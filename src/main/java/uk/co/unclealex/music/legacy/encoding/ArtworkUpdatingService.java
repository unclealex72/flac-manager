package uk.co.unclealex.music.legacy.encoding;

import java.io.File;
import java.util.SortedSet;

import uk.co.unclealex.music.legacy.Encoding;

public interface ArtworkUpdatingService {

	public boolean updateArtwork(SortedSet<File> flacFiles, SortedSet<File> possibleImageFiles);

	public void updateEncodedArtwork(Encoding encoding, File flacFile, File encodedDestination);

}
