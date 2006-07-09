/**
 * 
 */
package uk.co.unclealex.flacconverter;

import org.apache.log4j.Logger;


/**
 * @author alex
 *
 */
public interface FormatDAO {

	public IterableIterator<Track> findAllTracks(Logger log);	
}
