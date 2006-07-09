import java.io.File;
import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import uk.co.unclealex.flacconverter.FileBasedDAO;
import uk.co.unclealex.flacconverter.FlacDAO;
import uk.co.unclealex.flacconverter.IterableIterator;
import uk.co.unclealex.flacconverter.Mp3FileCodec;
import uk.co.unclealex.flacconverter.Track;

/**
 * @author alex
 *
 */
public class TrackTest {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		Logger log = Logger.getLogger("flac");
		FileBasedDAO manager = new FileBasedDAO(new File("/mnt/home/alex/raw/"), new Mp3FileCodec());
		IterableIterator<Track> tracks = manager.findAllTracks(log);
		SortedSet<Track> flacTracks = new TreeSet<Track>();
		for (Track track : new FlacDAO().findAllTracks(log)) {
			flacTracks.add(track);
		}
		for (Track track : tracks) {
			if (flacTracks.contains(track)) {
				System.out.println(track);
			}
		}
	}
}
