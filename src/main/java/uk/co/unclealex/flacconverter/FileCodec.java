/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.File;
import java.util.List;

/**
 * @author alex
 *
 */
public interface FileCodec {

	public String getExtension();
	
	public String[] generateTagCommand(File file);
	
	public Track processTagCommandOutput(File file, List<String> output) throws InvalidTrackException;

	public String[] generateEncodeCommand(Track track, File out);

	public File getAlbumDirectory(File baseDirectory, Album album);
	
	public File getFile(File baseDirectory, Track track);
}
