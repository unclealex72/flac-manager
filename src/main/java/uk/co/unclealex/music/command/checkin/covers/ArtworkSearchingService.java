package uk.co.unclealex.music.command.checkin.covers;

import java.io.IOException;
import java.net.URI;

import uk.co.unclealex.music.MusicFile;

/**
 * An interface for finding artwork on Amazon.
 * @author alex
 *
 */
public interface ArtworkSearchingService {

  /**
   * Find the URI of the artwork for a {@link MusicFile}.
   * @param musicFile The tagging information used to find cover art.
   * @return The URI of the artwork for the product or null if it could not be found.
   * @throws IOException
   */
	public URI findArtwork(MusicFile musicFile) throws IOException;

}
