package uk.co.unclealex.music.checkin.covers;

import java.io.IOException;
import java.net.URI;

/**
 * An interface for finding artwork on Amazon.
 * @author alex
 *
 */
public interface ArtworkSearchingService {

  /**
   * Find the URI of the artwork for an Amazon ID.
   * @param asin The Amazon ID.
   * @return The URI of the artwork for the product or null if it could not be found.
   * @throws IOException
   */
	public URI findArtwork(String asin) throws IOException;

}
