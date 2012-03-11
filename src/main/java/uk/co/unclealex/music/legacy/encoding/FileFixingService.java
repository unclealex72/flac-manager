package uk.co.unclealex.music.legacy.encoding;

import java.io.File;
import java.util.SortedSet;

/**
 * A service to make sure that flac filenames are consistent with
 * <code>artist_name/album_name/track_title.flac</code>. All characters are
 * lower case, non-accented latin characters with whitespaces replaced by
 * <code>_</code>. In the case of an album consiting of various artists the
 * <code>artist_name</code> is <code>various</code>
 * 
 * @author alex
 * 
 */
public interface FileFixingService {

	/**
	 * Fix flac filenames. First make sure that the
	 * <code>ALBUMARTIST<code> tag is set for any track under a directory called <code>Various</code>
	 * . Each file's name then is normalised and the file is moved to this
	 * location. Picture or owner files in the same directories as any of the flac
	 * files are also moved. Any directories that are left empty due to this
	 * procedure are removed.
	 * 
	 * @param flacFiles
	 *          The files to fix.
	 */
	public void fixVariousArtistsAndFlacFilenames(SortedSet<File> flacFiles);

	/**
	 * Create a normalised filename given the supplied information.
	 * 
	 * @param artist
	 *          The name of the artist.
	 * @param album
	 *          The album title.
	 * @param compilation
	 *          True if this track is part of a compilation, false otherwise.
	 * @param trackNumber
	 *          The track number.
	 * @param title
	 *          The title of the track.
	 * @return An absolute file whose name is normalised.
	 */
	public File getFixedFlacFilename(String artist, String album, boolean compilation, int trackNumber, String title);
}
