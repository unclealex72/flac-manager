/**
 * 
 */
package uk.co.unclealex.flacconverter.main;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import uk.co.unclealex.flacconverter.Album;
import uk.co.unclealex.flacconverter.Constants;
import uk.co.unclealex.flacconverter.FileBasedDAO;
import uk.co.unclealex.flacconverter.FileCodec;
import uk.co.unclealex.flacconverter.FlacDAO;
import uk.co.unclealex.flacconverter.FlacIOUtils;
import uk.co.unclealex.flacconverter.Mp3FileCodec;
import uk.co.unclealex.flacconverter.OggFileCodec;
import uk.co.unclealex.flacconverter.SlimServerConfig;
import uk.co.unclealex.flacconverter.Track;

/**
 * @author alex
 *
 */
public class Convert implements Runnable {

	private static File LOCK_FILE = new File("/tmp/flacconvert.lock");
	private SortedSet<Track> i_flacTracks;
	private FileCodec i_codec;
	private Logger i_log;
	private FileBasedDAO i_fileBasedDAO;
	private File i_baseDir;
	private Map<String, SortedSet<Album>> i_ownedAlbums;
	
	private Convert(SortedSet<Track> flacTracks, FileCodec codec, Map<String, SortedSet<Album>> ownedAlbums) {
		i_flacTracks = flacTracks;
		i_codec = codec;
		String extension = codec.getExtension();
		i_log = Logger.getLogger(extension);
		i_baseDir = new File(Constants.BASE_DIR, extension);
		i_fileBasedDAO = new FileBasedDAO(new File(i_baseDir, Constants.RAW_DIR), i_codec);
		i_ownedAlbums = ownedAlbums;
	}
	
	public void run() {
		Logger log = getLog();
		log.info("Scanning existing " + getCodec().getExtension() + " files.");
		SortedSet<Track> tracks = new TreeSet<Track>();
		
		for (Track track : getFileBasedDAO().findAllTracks(log)) {
			tracks.add(track);
		}

		SortedSet<Track> newTracks = new TreeSet<Track>();
		SortedSet<Track> deletedTracks = new TreeSet<Track>();
		newTracks.addAll(getFlacTracks());
		newTracks.removeAll(tracks);
		deletedTracks.addAll(tracks);
		deletedTracks.removeAll(getFlacTracks());
		
		int countNewTracks = newTracks.size();
		int countDeletedTracks = deletedTracks.size();
		log.info("Found " + countNewTracks + " new " + pluralise("track", "tracks", countNewTracks) + ".");
		for (Track track : newTracks) {
			log.debug(track.toString());
		}
		log.info("Found " + countDeletedTracks + " " + pluralise("track", "tracks", countDeletedTracks) + " to delete.");
		for (Track track : deletedTracks) {
			log.debug(track.toString());
		}

		SortedSet<Track> reencodedTracks = new TreeSet<Track>();
		
		Map<String, Track> flacTracksByKey = Track.makeMap(getFlacTracks());
		Map<String, Track> tracksByKey = Track.makeMap(tracks);
		
		for (Map.Entry<String, Track> entry : flacTracksByKey.entrySet()) {
			Track track = tracksByKey.get(entry.getKey());
			if (track != null) {
				Track flacTrack = entry.getValue();
				if (flacTrack.getLastModified() > track.getLastModified()) {
					reencodedTracks.add(flacTrack);
				}
			}
		}
		int countReencodedTracks = reencodedTracks.size();
		log.info("Found " + countReencodedTracks + " reencoded " + pluralise("track", "tracks", countReencodedTracks) + ".");
		for (Track track : reencodedTracks) {
			log.debug(track.toString());
			newTracks.add(track);
		}
		
		int trackIdx = 1;
		for (Track track : newTracks) {
			log.info("Converting " + progress(trackIdx++, countNewTracks + countReencodedTracks, track));
			try {
				String output = encode(track);
				log.debug(output);
			}
			catch (IOException e) {
				log.warn("Could not encode " + track.getFile(), e);
			}
		}
		
		trackIdx = 1;
		for (Track track : deletedTracks) {
			log.info("Deleting " + progress(trackIdx++, countDeletedTracks, track));
			FlacIOUtils.deleteFile(track.getFile(), log);
		}
		
		log.info("Pruning empty directories.");
		FlacIOUtils.pruneDirectories(getFileBasedDAO().getBaseDirectory(), getLog());
	
		log.info("Removing personal directories.");
		for (File personalDirectory : getBaseDir().listFiles(s_personalFileFilter)) {
			try {
				FlacIOUtils.runCommand(
						new String[] {
								"find", "-L", personalDirectory.getAbsolutePath(), "-depth",
								"-xtype", "l", "-exec", "rm", "{}", ";"
						},
						log);
			} catch (IOException e) {
				log.warn("Could not delete the " + personalDirectory.getAbsolutePath() + " personal directory.", e);
			}
			FlacIOUtils.pruneDirectories(personalDirectory, log);
		}
		
		log.info("Recreating personal directories.");
		for (Map.Entry<String, SortedSet<Album>> entry : getOwnedAlbums().entrySet()) {
			String owner = entry.getKey();
			File ownerDir = new File(getBaseDir(), owner);
			for (Album album : entry.getValue()) {
				File targetDir = getCodec().getAlbumDirectory(getFileBasedDAO().getBaseDirectory(), album);
				File sourceDir = getCodec().getAlbumDirectory(ownerDir, album);
				sourceDir.getParentFile().mkdirs();
				try {
					FlacIOUtils.runCommand(new String[] { "ln", "-s", targetDir.getAbsolutePath(), sourceDir.getAbsolutePath()}, getLog());
				} catch (IOException e) {
					log.warn("Could not link artist " + album + " from " + sourceDir.getAbsolutePath());
				}
			}
		}
		log.info("Finished");
	}

	private String encode(Track track) throws IOException {
		File target = getCodec().getFile(getFileBasedDAO().getBaseDirectory(), track);
		if (target.exists()) {
			target.delete();
		}
		target.getParentFile().mkdirs();
		InputStream in = FlacIOUtils.runCommand(getCodec().generateEncodeCommand(track, target), getLog());
		String output = FlacIOUtils.toString(in);
		in.close();
		return output;
	}
	
	private String pluralise(String singular, String plural, int scalar) {
		return scalar==1?singular:plural;
	}
	
	private String format(String format, Object ... args) {
		Formatter formatter = new Formatter();
		formatter.format(format, args);
		formatter.flush();
		return formatter.toString();
	}
	private String progress(int current, int total, Track track) {
		return format(
				"%d of %d (%3.2f%%) %s", current, total, 100 * current / (double) total,
				track.toString()).toString();
	}
	/**
	 * @return the codec
	 */
	public FileCodec getCodec() {
		return i_codec;
	}

	/**
	 * @return the baseDir
	 */
	public File getBaseDir() {
		return i_baseDir;
	}

	/**
	 * @return the formatManager
	 */
	public FileBasedDAO getFileBasedDAO() {
		return i_fileBasedDAO;
	}

	/**
	 * @return the log
	 */
	public Logger getLog() {
		return i_log;
	}

	/**
	 * @return the flacTracks
	 */
	public SortedSet<Track> getFlacTracks() {
		return i_flacTracks;
	}
	
	/**
	 * @return the ownedArtists
	 */
	public Map<String, SortedSet<Album>> getOwnedAlbums() {
		return i_ownedAlbums;
	}

	public static void main(String[] args) {
		int retVal = go(args);
		if (LOCK_FILE.exists() && retVal != 2) {
			LOCK_FILE.delete();
		}
		if (retVal != 0) { 
			System.exit(retVal);
		}
	}
	
	/**
	 * @param args
	 */
	public static int go(String[] args) {
		Logger log = Logger.getLogger("flac");
		if (args.length != 1) {
			log.fatal("You must supply a configuration file");
			return 1;
		}
		try {
			if (LOCK_FILE.exists()) {
				log.fatal("Another instance of the flac converter is already running. Exiting now.");
				return 2;
			}
			try {
				if (LOCK_FILE.createNewFile()) {
					LOCK_FILE.deleteOnExit();
				}
			} catch (IOException e) {
				log.warn("Could not create the lock file.", e);
			}
			SlimServerConfig config = new SlimServerConfig();
			try {
				config.initialise(new File(args[0]));
			} catch (IOException e) {
				log.fatal("Cannot open the config file", e);
				return 1;
			}
			
			List<String> extensions = new LinkedList<String>();
			extensions.addAll(Arrays.asList(args));
			extensions.remove(0);
			List<FileCodec> codecs = findCodecs(extensions, config.getDefiniteArticles()); 

			log.info("Scanning flac tracks");
			SortedSet<Track> flacTracks = new TreeSet<Track>();
			for (Track track : new FlacDAO().findAllTracks(log)) {
				flacTracks.add(track);
			}
			
			for (FileCodec codec : codecs) {
				Runnable runnable = new Convert(flacTracks, codec, analyseOwnership(log));
				new Thread(runnable).start();
			}
		} catch (RuntimeException e) {
			e.printStackTrace(System.err);
			log.fatal("An error has caused the flac converter to stop running.", e);
			return 1;
		}
		return 0;
	}

	private static final FilenameFilter s_personalFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.startsWith("owner.");
		}
	};

	private static Map<String, SortedSet<Album>> analyseOwnership(Logger log) {
		log.info("Analysing ownership");
		TreeMap<String, SortedSet<Album>> ownedAlbums = new TreeMap<String, SortedSet<Album>>();
		SortedSet<Album> unownedAlbums = new TreeSet<Album>();
		Map<File,Album> albums = new FlacDAO().getAllAlbums(log);
		
		for (Map.Entry<File, Album> entry : albums.entrySet()) {
			File dir = entry.getKey();
			Album album = entry.getValue();
			List<File> ownerFiles = new LinkedList<File>(Arrays.asList(dir.listFiles(s_personalFileFilter)));
			ownerFiles.addAll(Arrays.asList(dir.getParentFile().listFiles(s_personalFileFilter)));
			if (ownerFiles.size() == 0) {
				unownedAlbums.add(album);
			}
			else {
				for (File ownerFile : ownerFiles) {
					String owner = ownerFile.getName();
					if (ownedAlbums.get(owner) == null) {
						ownedAlbums.put(owner, new TreeSet<Album>());
					}
					ownedAlbums.get(owner).add(album);
				}
			}
		}
		
		for (Map.Entry<String, SortedSet<Album>> entry : ownedAlbums.entrySet()) {
			log.info("Found " + entry.getKey());
			for (Album album : entry.getValue()) {
				log.debug(album);
			}
		}
		
		if (!unownedAlbums.isEmpty()) {
			StringBuffer message = new StringBuffer("The following albums are owned by no-one:\n");
			for (Album album : unownedAlbums) {
				message.append(album).append('\n');
			}
			log.warn(message.toString());
		}
		return ownedAlbums;
	}

	/**
	 * @param args
	 * @return
	 */
	private static List<FileCodec> findCodecs(Collection<String> extensions, Collection<String> definiteArticles) {
		FileCodec[] codecs = new FileCodec[] { new OggFileCodec(definiteArticles), new Mp3FileCodec(definiteArticles) };

		if (extensions == null || extensions.size() == 0) {
			return Arrays.asList(codecs);
		}
		List<FileCodec> foundCodecs = new ArrayList<FileCodec>();
		for (String extension : extensions) {
			for (FileCodec codec : codecs) {
				if (extension.equalsIgnoreCase(codec.getExtension())) {
					foundCodecs.add(codec);
				}
			}
		}
		return foundCodecs;
	}

}
