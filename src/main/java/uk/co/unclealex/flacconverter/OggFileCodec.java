/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.File;


/**
 * @author alex
 *
 */
public class OggFileCodec extends AbstractFileCodec {

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

	public File getArtistDirectory(File dir, String artist) {
		artist = IOUtils.sanitise(artist);
		return new File(dir, artist);
	}

}
