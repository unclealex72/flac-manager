package uk.co.unclealex.flacconverter.main;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;

import uk.co.unclealex.flacconverter.FileBasedDAO;
import uk.co.unclealex.flacconverter.FileCodec;
import uk.co.unclealex.flacconverter.IOUtils;
import uk.co.unclealex.flacconverter.IterableIterator;
import uk.co.unclealex.flacconverter.Mp3FileCodec;
import uk.co.unclealex.flacconverter.OggFileCodec;
import uk.co.unclealex.flacconverter.SlimServerConfig;
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
		SlimServerConfig config = new SlimServerConfig();
		try {
			config.initialise(new File(args[0]));
		} catch (IOException e) {
			log.fatal("Cannot open the config file", e);
			System.exit(1);
		}
		Collection<String> definiteArticles = config.getDefiniteArticles();
		for (FileCodec codec : new FileCodec[] { new Mp3FileCodec(definiteArticles), new OggFileCodec(definiteArticles) }) {
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
