package uk.co.unclealex.music.command.checkin.covers;

import java.net.URI;

import uk.co.unclealex.music.DataObject;

/**
 * A cover art image that can be downloaded from Amazon.
 * @author alex
 *
 */
public class ExternalCoverArtImage extends DataObject {

  /**
   * The URI where the image can be downloaded.
   */
	private final URI uri;
	
	/**
	 * The area of the image that can be downloaded.
	 */
	private final int size;
	
	/**
	 * Instantiates a new external cover art image.
	 *
	 * @param uri the uri
	 * @param size the size
	 */
	public ExternalCoverArtImage(URI uri, int size) {
		super();
		this.uri = uri;
		this.size = size;
	}

	/**
	 * Gets the URL where the image can be downloaded.
	 *
	 * @return the URL where the image can be downloaded
	 */
	public URI getUri() {
		return uri;
	}
	
	/**
	 * Gets the area of the image that can be downloaded.
	 *
	 * @return the area of the image that can be downloaded
	 */
	public int getSize() {
		return size;
	}
}
