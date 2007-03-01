/**
 * 
 */
package uk.co.unclealex.flacconverter.image;

import java.util.Collection;


/**
 * @author alex
 *
 */
public interface SearchManager {

	public Collection<Album> search(String artist, String title);
}
