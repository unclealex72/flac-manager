package uk.co.unclealex.music;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.encoding.RenamingService;
import uk.co.unclealex.music.jaxb.artiststracks.ArtistTracks;
import uk.co.unclealex.music.jaxb.artiststracks.ArtistsTracks;
import uk.co.unclealex.music.jaxb.artiststracks.ObjectFactory;

public class ArtistFixingServiceImpl implements ArtistFixingService {

	private static final Logger log = LoggerFactory.getLogger(ArtistFixingServiceImpl.class);
	
	private File i_flacDirectory;
	private RenamingService i_renamingService;
	
	@Override
	public SortedSet<String> listArtists(final File cacheFile) throws IOException {
		Map<String, Set<String>> tracksByArtist = new HashMap<String, Set<String>>();
		@SuppressWarnings("unchecked")
		Collection<File> flacFiles = FileUtils.listFiles(getFlacDirectory(), new String[] { Constants.FLAC }, true);
		for (File flacFile : flacFiles) {
			Tag tag = readFile(flacFile).getTag();
			String artist = tag.getFirst(FieldKey.ARTIST);
			Set<String> tracks = tracksByArtist.get(artist);
			if (tracks == null) {
				tracks = new HashSet<String>();
				tracksByArtist.put(artist, tracks);
			}
			tracks.add(flacFile.getAbsolutePath());
		}
		ObjectFactory objectFactory = new ObjectFactory();
		final ArtistsTracks artistsTracks = objectFactory.createArtistsTracks();
		List<ArtistTracks> artistTracksList = artistsTracks.getArtistTracks();
		for (Entry<String, Set<String>> entry : tracksByArtist.entrySet()) {
			String artist = entry.getKey();
			Set<String> tracks = entry.getValue();
			ArtistTracks artistTracks = objectFactory.createArtistTracks();
			artistTracks.setArtist(artist);
			artistTracks.getTracks().addAll(tracks);
			artistTracksList.add(artistTracks);
		}
		writeArtistsTracks(cacheFile, artistsTracks);
		final Comparator<String> ignoreCaseComparator = new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return normalise(s1).compareTo(normalise(s2));
			}
			
			protected String normalise(String str) {
				return str.toLowerCase().trim().replaceAll("^(the|a) ", "");
			}
		};
		TreeSet<String> artists = new TreeSet<String>(ignoreCaseComparator);
		artists.addAll(tracksByArtist.keySet());
		return artists;
	}

	protected void writeArtistsTracks(final File cacheFile, final ArtistsTracks artistsTracks) throws IOException {
		JaxbCallback<Object> callback = new JaxbCallback<Object>() {
			@Override
			public Object doInJaxbContext(JAXBContext ctxt) throws JAXBException, IOException {
				ctxt.createMarshaller().marshal(artistsTracks, cacheFile);
				return null;
			}
		};
		execute(callback);
	}

	protected interface JaxbCallback<R> {
		public R doInJaxbContext(JAXBContext ctxt) throws JAXBException, IOException;
	}
	
	protected <R> R execute(JaxbCallback<R> callback) throws IOException {
		try {
			JAXBContext ctxt = JAXBContext.newInstance(ArtistsTracks.class);
			return callback.doInJaxbContext(ctxt);
		}
		catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	protected AudioFile readFile(File flacFile) throws IOException {
		try {
			return AudioFileIO.read(flacFile);
		}
		catch (CannotReadException e) {
			throw new IOException(e);
		}
		catch (TagException e) {
			throw new IOException(e);
		}
		catch (ReadOnlyFileException e) {
			throw new IOException(e);
		}
		catch (InvalidAudioFrameException e) {
			throw new IOException(e);
		}
	}
	
	
	@Override
	public void fixArtists(Map<String, String> newArtistNamesByOriginalArtistName, final File cacheFile) throws IOException {
		JaxbCallback<ArtistsTracks> callback = new JaxbCallback<ArtistsTracks>() {
			@Override
			public ArtistsTracks doInJaxbContext(JAXBContext ctxt) throws JAXBException, IOException {
				return (ArtistsTracks) ctxt.createUnmarshaller().unmarshal(cacheFile);
			}
		};
		ArtistsTracks artistsTracks = execute(callback);
		Transformer<String, File> transformer = new Transformer<String, File>() {
			@Override
			public File transform(String path) {
				return new File(path);
			}
		};
		RenamingService renamingService = getRenamingService();
		List<ArtistTracks> artistTracksList = artistsTracks.getArtistTracks();
		for (Entry<String, String> entry : newArtistNamesByOriginalArtistName.entrySet()) {
			String originalArtistName = entry.getKey();
			String newArtistName = entry.getValue();
			Predicate<ArtistTracks> originalArtistPredicate = createArtistPredicate(originalArtistName);
			ArtistTracks artistTracks = CollectionUtils.find(artistTracksList, originalArtistPredicate);
			if (artistTracks != null) {
				log.info("Renaming artist " + originalArtistName + " to " + newArtistName);
				SortedSet<File> flacFiles = CollectionUtils.collect(artistTracks.getTracks(), transformer, new TreeSet<File>());
				renamingService.rename(flacFiles, newArtistName, null, null, null, null);
				artistTracksList.remove(artistTracks);
			}
			else {
				log.warn("Ignoring missing artist " + originalArtistName);
			}
		}
		writeArtistsTracks(cacheFile, artistsTracks);
	}

	protected Predicate<ArtistTracks> createArtistPredicate(final String artist) {
		Predicate<ArtistTracks> predicate = new Predicate<ArtistTracks>() {
			@Override
			public boolean evaluate(ArtistTracks artistTracks) {
				return artist.equals(artistTracks.getArtist());
			}
		};
		return predicate;
	}
	
	public File getFlacDirectory() {
		return i_flacDirectory;
	}

	public void setFlacDirectory(File flacDirectory) {
		i_flacDirectory = flacDirectory;
	}

	public RenamingService getRenamingService() {
		return i_renamingService;
	}

	public void setRenamingService(RenamingService renamingService) {
		i_renamingService = renamingService;
	}
}
