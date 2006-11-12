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
public class Mp3FileCodec extends AbstractFileCodec {

	/**
	 * @param definiteArticles
	 */
	public Mp3FileCodec(Collection<String> definiteArticles) {
		super(definiteArticles);
	}

	public String getExtension() {
		return "mp3";
	}

	public String[] generateTagCommand(File file) {
		return new String[] { "id3v2", "-l", file.getAbsoluteFile().getPath() };
	}

	@Override
	public String getAlbumPattern() {
		return "TALB \\(Album/Movie/Show title\\): (.*)";
	}

	@Override
	public String getArtistPattern() {
		return "TPE1 \\(Lead performer\\(s\\)/Soloist\\(s\\)\\): (.*)";
	}

	@Override
	public String getGenrePattern() {
		return "TCON \\(Content type\\): (.*) \\([0-9]+\\)";
	}

	@Override
	public String getTitlePattern() {
		return "TIT2 \\(Title/songname/content description\\): (.*)";
	}
	
	@Override
	public String getTrackPattern() {
		return "TRCK \\(Track number/Position in set\\): (.*)";
	}

	@Override
	public String getYearPattern() {
		return "TYER (Year): (.*)";
	}
}
