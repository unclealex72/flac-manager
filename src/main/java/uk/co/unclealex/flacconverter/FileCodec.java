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
	
	public Track processTagCommandOutput(File file, List<String> output);

	public String[] generateEncodeCommand(Track track, File out);
}
