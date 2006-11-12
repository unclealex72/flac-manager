/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.File;
import java.util.Collection;


/**
 * @author alex
 *
 */
public class OggFileCodec extends AbstractFileCodec {

	/**
	 * @param definiteArticles
	 */
	public OggFileCodec(Collection<String> definiteArticles) {
		super(definiteArticles);
	}

	public String getExtension() {
		return "ogg";
	}

	public String[] generateTagCommand(File file) {
		return new String[] { "vorbiscomment", "-l", file.getAbsoluteFile().getPath() };
	}

	@Override
	public String getAlbumPattern() {
		return "ALBUM=(.*)";
	}

	@Override
	public String getArtistPattern() {
		return "ARTIST=(.*)";
	}

	@Override
	public String getGenrePattern() {
		return "GENRE=(.*)";
	}

	@Override
	public String getTitlePattern() {
		return "TITLE=(.*)";
	}

	@Override
	public String getTrackPattern() {
		return "TRACKNUMBER=(.*)";
	}

	@Override
	public String getYearPattern() {
		return "DATE=(.*)";
	}	
}
