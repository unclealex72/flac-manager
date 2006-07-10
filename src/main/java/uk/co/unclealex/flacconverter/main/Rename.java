package uk.co.unclealex.flacconverter.main;
import java.io.File;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import uk.co.unclealex.flacconverter.FileBasedDAO;
import uk.co.unclealex.flacconverter.FileCodec;
import uk.co.unclealex.flacconverter.IOUtils;
import uk.co.unclealex.flacconverter.IterableIterator;
import uk.co.unclealex.flacconverter.Mp3FileCodec;
import uk.co.unclealex.flacconverter.OggFileCodec;
import uk.co.unclealex.flacconverter.Track;

/**
 * @author alex
 *
 */
public class Rename {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		Logger log = Logger.getLogger(Rename.class);
		for (FileCodec codec : new FileCodec[] { new Mp3FileCodec(), new OggFileCodec() }) {
			FileBasedDAO dao = new FileBasedDAO(new File("/mnt/multimedia/converted/" + codec.getExtension() + "/raw"), codec);
			IterableIterator<Track> tracks = dao.findAllTracks(log);
			for (Track track : tracks) {
				File source = track.getFile();
				File target = codec.getFile(dao.getBaseDirectory(), track);
				if (!source.equals(target)) {
					System.out.println("Moving " + source + " -> " + target);
					target.getParentFile().mkdirs();
					source.renameTo(target);
				}
			}
			IOUtils.pruneDirectories(dao.getBaseDirectory(), log);
		}
	}
}
