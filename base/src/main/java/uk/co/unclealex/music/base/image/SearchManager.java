/**
 * 
 */
package uk.co.unclealex.music.base.image;

import java.util.Collection;


/**
 * @author alex
 *
 */
public interface SearchManager {

	public Collection<Album> search(String artist, String title);
}
