/**
 * 
 */
package uk.co.unclealex.flacconverter.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import uk.co.unclealex.flacconverter.FlacAlbum;
import uk.co.unclealex.flacconverter.FlacDAO;
import uk.co.unclealex.flacconverter.image.Album;
import uk.co.unclealex.flacconverter.image.AlbumComparator;
import uk.co.unclealex.flacconverter.image.SearchManager;
import uk.co.unclealex.flacconverter.image.amazon.AmazonSearchManager;

/**
 * @author alex
 *
 */
public class MissingCovers {

	private static Logger log = Logger.getLogger(MissingCovers.class);
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException, IOException {
		Set<FlacAlbum> flacAlbums = new FlacDAO().getAllAlbums(log);
		SearchManager manager = new AmazonSearchManager();
		
		for (FlacAlbum flacAlbum : flacAlbums) {
			File coverDirectory = new File(flacAlbum.getDirectory(), "covers");
			if (!coverDirectory.exists()) {
				List<Album> albums = new LinkedList<Album>();
				String artist = flacAlbum.getArtist();
				String title = flacAlbum.getTitle();
				for (Album album : manager.search(artist, title)) {
					if (album.getImageUrl() != null) {
						albums.add(album);
					}
				}
				
				coverDirectory.mkdirs();
				if (albums.isEmpty()) {
					System.out.println("Found no covers for " + artist + ", " + title);
				}
				else {
					System.out.println("Found " + albums.size() + " cover(s) for " + artist + ", " + title);
					int idx = 1;
					Album[] sortedAlbums = albums.toArray(new Album[albums.size()]);
					Arrays.sort(sortedAlbums, new AlbumComparator(artist, title));
					for (Album album : sortedAlbums) {
						String basename = new Formatter().format("cover%03d", idx++).toString();
						File f = new File(coverDirectory, basename + ".jpg");
						FileOutputStream out = new FileOutputStream(f);
						InputStream in = new URL(album.getImageUrl()).openStream();
						IOUtils.copy(in, out);
						out.flush();
						in.close();
						out.close();
					}
				}
			}
		}
	}
}
