package uk.co.unclealex.music.gwt.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.springframework.web.context.ServletContextAware;

import uk.co.unclealex.music.Constants;
import uk.co.unclealex.music.covers.ArtworkSearchingService;
import uk.co.unclealex.music.gwt.client.GwtAlbumCoverService;
import uk.co.unclealex.music.gwt.client.action.Action;
import uk.co.unclealex.music.gwt.client.action.ActionFactory;
import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;
import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;


public class GwtAlbumCoverServiceImpl implements GwtAlbumCoverService, ServletContextAware {

	private File i_flacDirectory;
	private ServletContext i_servletContext;
	private ArtworkSearchingService i_artworkSearchingService;
	
	public SortedMap<AlbumInformationBean, String> listMissingCovers(ActionFactory<Object> actionFactory) throws IOException {
		File missingArtworkFile = new File(getFlacDirectory(), Constants.MISSING_ARTWORK);
		SortedMap<AlbumInformationBean, String> albumInformationBeans = new TreeMap<AlbumInformationBean, String>();
		if (missingArtworkFile.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(missingArtworkFile));
				String line;
				while ((line = reader.readLine()) != null) {
					AlbumInformationBean albumInformationBean = createAlbumInformationBean(line);
					albumInformationBeans.put(albumInformationBean, createToken(actionFactory, albumInformationBean));
				}
			}
			finally {
				IOUtils.closeQuietly(reader);
			}
		}
		return albumInformationBeans;
	}

	public SortedMap<Character, String> listFirstLetters(ActionFactory<Character> actionFactory) throws IOException {
		SortedMap<Character, String> firstLetters = new TreeMap<Character, String>();
		for (File file : getFlacDirectory().listFiles(new DirectoryFilter())) {
			String name = removeArticle(file.getName());
			char firstLetter = name.charAt(0);
			firstLetters.put(firstLetter, createToken(actionFactory, firstLetter));
		}
		return firstLetters;
	}
	
	protected String removeArticle(String name) {
		return name.startsWith("the_")?name.substring(4):name;
	}

	public SortedMap<AlbumInformationBean, String> listAlbums(ArtistInformationBean artistInformationBean, ActionFactory<AlbumInformationBean> actionFactory) throws IOException {
		SortedMap<AlbumInformationBean, String> albumInformationBeans = new TreeMap<AlbumInformationBean, String>();
		File flacDirectory = getFlacDirectory();
		for (File file : new File(flacDirectory, artistInformationBean.getUrl()).listFiles(new DirectoryFilter())) {
			String relativePath = flacDirectory.toURI().relativize(file.toURI()).getPath();
			Tag tag = findFirstTag(file);
			if (tag != null) {
				AlbumInformationBean albumInformationBean = 
					new AlbumInformationBean(artistInformationBean, tag.getFirst(FieldKey.ALBUM), relativePath);
				albumInformationBeans.put(albumInformationBean, createToken(actionFactory, albumInformationBean));
			}
		}
		return albumInformationBeans;
	}

	protected <T> String createToken(ActionFactory<T> actionFactory, T parameter) throws IOException {
		return serialise(actionFactory.createAction(parameter));
	}
	
	public SortedMap<ArtistInformationBean, String> listArtists(char firstLetter, ActionFactory<ArtistInformationBean> actionFactory) throws IOException {
		SortedMap<ArtistInformationBean, String> artistInformationBeans = new TreeMap<ArtistInformationBean, String>();
		File flacDirectory = getFlacDirectory();
		for (File file : flacDirectory.listFiles(new DirectoryFilter())) {
			String artistName = removeArticle(file.getName());
			if (artistName.charAt(0) == firstLetter) {
				String relativePath = flacDirectory.toURI().relativize(file.toURI()).getPath();
				Tag tag = findFirstTag(file);
				if (tag != null) {
					ArtistInformationBean artistInformationBean = 
						new ArtistInformationBean(tag.getFirst(FieldKey.ARTIST), relativePath);
					artistInformationBeans.put(artistInformationBean, createToken(actionFactory, artistInformationBean));
				}
			}
		}
		return artistInformationBeans;
	}
	
	protected class DirectoryFilter implements FileFilter {
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	}
	
	protected AlbumInformationBean createAlbumInformationBean(String relativePath) throws IOException {
		Tag tag = findFirstTag(relativePath);
		AlbumInformationBean albumInformationBean = null;
		if (tag != null) {
			albumInformationBean = createAlbumInformationBeanFromTags(null, relativePath, tag);
		}
		return albumInformationBean;
	}

	protected AlbumInformationBean createAlbumInformationBeanFromTags(ArtistInformationBean artistInformationBean, String relativePath, Tag tag)
			throws KeyNotFoundException {
		if (artistInformationBean == null) {
			artistInformationBean = new ArtistInformationBean(tag.getFirst(FieldKey.ARTIST), relativePath.substring(relativePath.indexOf('/')));
		}
		return new AlbumInformationBean(artistInformationBean, tag.getFirst(FieldKey.ALBUM), relativePath);
	}

	protected Tag findFirstTag(String relativePath) throws IOException {
		return findFirstTag(new File(getFlacDirectory(), relativePath));
	}
	
	protected Tag findFirstTag(File directory) throws IOException {
		Tag tag = null;
		File[] childFiles = directory.listFiles();
		for (int idx = 0; tag == null && idx < childFiles.length; idx++) {
			File file = childFiles[idx];
			if ("flac".equals(FilenameUtils.getExtension(file.getName()))) {
				AudioFile audioFile;
				try {
					audioFile = AudioFileIO.read(file);
				}
				catch (CannotReadException e) {
					throw new IOException("Cannot read tags from audio file " + file, e);
				}
				catch (TagException e) {
					throw new IOException("Cannot read tags from audio file " + file, e);
				}
				catch (ReadOnlyFileException e) {
					throw new IOException("Cannot read tags from audio file " + file, e);
				}
				catch (InvalidAudioFrameException e) {
					throw new IOException("Cannot read tags from audio file " + file, e);
				}
				tag = audioFile.getTag();
			}
		}
		for (int idx = 0; tag == null && idx < childFiles.length; idx++) {
			File file = childFiles[idx];
			if (file.isDirectory()) {
				tag = findFirstTag(file);
			}
		}
		return tag;
	}

	public String createArtworkLink(String relativePath) {
		return getServletContext().getContextPath() + "/artwork/" + relativePath;
	}
	
	public String serialise(Action action) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new GZIPOutputStream(out));
		objectOutputStream.writeObject(action);
		objectOutputStream.close();
		return new String(Base64.encodeBase64(out.toByteArray()), "ascii");
	}
	
	public Action deserialise(String token) throws IOException {
		byte[] data = Base64.decodeBase64(token.getBytes());
		ObjectInputStream objectInputStream = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(data)));
		Action action;
		try {
			action = (Action) objectInputStream.readObject();
		}
		catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
		return action;
	}

	public List<String> searchForArtwork(String artist, String album) throws IOException {
		return getArtworkSearchingService().findArtwork(artist, album);
	}
	
	public File getFlacDirectory() {
		return i_flacDirectory;
	}

	public void setFlacDirectory(File flacDirectory) {
		i_flacDirectory = flacDirectory;
	}

	public ServletContext getServletContext() {
		return i_servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		i_servletContext = servletContext;
	}

	public ArtworkSearchingService getArtworkSearchingService() {
		return i_artworkSearchingService;
	}

	public void setArtworkSearchingService(ArtworkSearchingService artworkSearchingService) {
		i_artworkSearchingService = artworkSearchingService;
	}
}
