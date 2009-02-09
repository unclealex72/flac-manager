/**
 * 
 */
package uk.co.unclealex.music.core.image;

import java.util.Collection;


/**
 * @author alex
 *
 */
public interface SearchManager {

	public Collection<Album> search(String artist, String title);
}
