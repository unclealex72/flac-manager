/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author alex
 *
 */
public abstract class AbstractFileCodec implements FileCodec {

	private static String TITLE = "title";
	private static String ARTIST = "artist";
	private static String ALBUM = "album";
	private static String TRACK = "track";
	private static String YEAR = "year";
	private static String GENRE = "genre";
	
	public Track processTagCommandOutput(File file, List<String> output) {
		Map<String,Pattern> patterns = new HashMap<String, Pattern>();
		patterns.put(TITLE, Pattern.compile(getTitlePattern()));
		patterns.put(ARTIST, Pattern.compile(getArtistPattern()));
		patterns.put(ALBUM, Pattern.compile(getAlbumPattern()));
		patterns.put(TRACK, Pattern.compile(getTrackPattern()));
		patterns.put(YEAR, Pattern.compile(getYearPattern()));
		patterns.put(GENRE, Pattern.compile(getGenrePattern()));
		
		Map<String,String> fields = new HashMap<String, String>();
		for (String line : output) {
			for(Map.Entry<String,Pattern> entry : patterns.entrySet()) {
				Matcher matcher = entry.getValue().matcher(line);
				if (matcher.matches()) {
					fields.put(entry.getKey(), matcher.group(1));
				}
			}
		}
		int track = fields.get(TRACK) == null?0:Integer.parseInt(fields.get(TRACK));
		int year = fields.get(YEAR) == null?0:Integer.parseInt(fields.get(YEAR));
		return new Track(file, fields.get(ARTIST), fields.get(ALBUM), fields.get(TITLE), track, year, fields.get(GENRE));
	}
	
	public String[] generateEncodeCommand(Track track, File out) {
		return new String[] { "flac2" + getExtension(), track.getFile().getAbsolutePath(), out.getAbsolutePath() };
	}
	
	public abstract String getTitlePattern();
	public abstract String getArtistPattern();
	public abstract String getAlbumPattern();
	public abstract String getTrackPattern();
	public abstract String getYearPattern();
	public abstract String getGenrePattern();
}
