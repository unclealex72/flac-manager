package uk.co.unclealex.music.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;

import uk.co.unclealex.music.base.model.AbstractFlacBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;

public class TestFlacProvider implements IOFileFilter {

	private Map<File, FlacTrackBean> i_allFlacTrackBeans;
	private Map<File, FlacAlbumBean> i_allFlacAlbumBeans;
	private Map<File, FlacArtistBean> i_allFlacArtistBeans;
	private int i_nextId;
	
	public File getWorkingDirectory() {
		return new File(new File(System.getProperty("java.io.tmpdir")), "testFlacProvider");
	}
	
	public void initialise(String testBase) throws IOException {
		File resourceDirectory = findResourceDirectory(testBase);
		File workingDirectory = getWorkingDirectory();
		removeAll(workingDirectory);
		FileUtils.copyDirectory(resourceDirectory, workingDirectory);
		update();
	}

	protected File findResourceDirectory(String testBase) throws IOException {
		String resourceName = "testdata/" + testBase + "/ident.txt";
		ClassLoader classLoader = getClass().getClassLoader();
		URL resourceUrl = classLoader.getResource(resourceName);
		if (resourceUrl == null) {
			throw new FileNotFoundException(resourceName);
		}
		URI resourceLocator;
		try {
			resourceLocator = resourceUrl.toURI();
		}
		catch (URISyntaxException e) {
			throw new IOException("Could not find " + resourceName, e);
		}
		if (resourceLocator == null) {
			throw new FileNotFoundException(resourceName);
		}
		File resourceFile;
		try {
			resourceFile = new File(resourceLocator);
		}
		catch (IllegalArgumentException e) {
			throw new FileNotFoundException(resourceLocator.toString());
		}
		File resourceDirectory = resourceFile.getParentFile();
		if (resourceDirectory == null) {
			throw new FileNotFoundException(resourceFile.getPath() + "/..");
		}
		return resourceDirectory;
	}
	
	public void touchFiles(String... paths) throws Exception {
		FileCallback callback = new FileCallback() {
			@Override
			public void doWithFile(File f) throws Exception {
				f.setLastModified(System.currentTimeMillis());
			}
		};
		executeFileCallback(callback, paths);
	}
	
	public void removeFiles(String... paths) throws Exception {
		FileCallback callback = new FileCallback() {
			@Override
			public void doWithFile(File f) throws Exception {
				if (f.exists()) {
					if (f.isDirectory()) {
						FileUtils.deleteDirectory(f);
					}
					else {
						f.delete();
					}
				}
			}
		};
		executeFileCallback(callback, paths);
	}

	protected void executeFileCallback(FileCallback callback, String... paths) throws Exception {
		File workingDirectory = getWorkingDirectory();
		for (String path : paths) {
			File f = new File(workingDirectory, path);
			callback.doWithFile(f);
		}
		update();
	}
	
	protected interface FileCallback {
		public void doWithFile(File f) throws Exception;
	}
	public void mergeResource(String testBase) throws IOException {
		File resourceDirectory = findResourceDirectory(testBase);
		FileUtils.copyDirectory(resourceDirectory, getWorkingDirectory());
		update();		
	}
	
	protected void update() {
		setNextId(0);
		setAllFlacAlbumBeans(new HashMap<File, FlacAlbumBean>());
		setAllFlacArtistBeans(new HashMap<File, FlacArtistBean>());
		setAllFlacTrackBeans(new HashMap<File, FlacTrackBean>());
		FileUtils.listFiles(getWorkingDirectory(), this, FileFilterUtils.trueFileFilter());
	}
	
	@Override
	public boolean accept(File dir, String name) {
		return accept(new File(dir, name));
	}
	
	@Override
	public boolean accept(File file) {
		if ("flac".equalsIgnoreCase(FilenameUtils.getExtension(file.getName()))) {
			AudioFile audioFile;
			try {
				audioFile = AudioFileIO.read(file);
			}
			catch (Throwable e) {
				throw new IllegalArgumentException("Could not read flac file " + file);
			}
			Tag tag = audioFile.getTag();
			addTrack(tag.getFirstArtist(), tag.getFirstAlbum(), tag.getFirstTrack(), tag.getFirstTitle(), file);
		}
		return true;
	}
	
	protected void addTrack(String artist, String album, String track, String title, File file) {
		FlacAlbumBean flacAlbumBean = findOrCreateAlbum(artist, album, file.getParentFile());
		FlacTrackBean flacTrackBean = new FlacTrackBean();
		setIdAndCode(flacTrackBean, title);
		flacTrackBean.setFlacAlbumBean(flacAlbumBean);
		flacTrackBean.setRawTitle(title.getBytes());
		flacTrackBean.setTimestamp(file.lastModified());
		flacTrackBean.setTrackNumber(Integer.parseInt(track));
		flacTrackBean.setType("flc");
		flacTrackBean.setUrl(file.toURI().toString());
		flacAlbumBean.getFlacTrackBeans().add(flacTrackBean);
		getAllFlacTrackBeans().put(file, flacTrackBean);
	}

	protected FlacAlbumBean findOrCreateAlbum(String artist, String album, File file) {
		Map<File, FlacAlbumBean> allFlacAlbumBeans = getAllFlacAlbumBeans();
		FlacAlbumBean flacAlbumBean = allFlacAlbumBeans.get(file);
		if (flacAlbumBean == null) {
			FlacArtistBean flacArtistBean = findOrCreateArtist(artist, file.getParentFile());
			flacAlbumBean = new FlacAlbumBean();
			setIdAndCode(flacAlbumBean, album);
			flacAlbumBean.setFlacArtistBean(flacArtistBean);
			flacAlbumBean.setFlacTrackBeans(new TreeSet<FlacTrackBean>());
			flacAlbumBean.setRawTitle(album.getBytes());
			flacArtistBean.getFlacAlbumBeans().add(flacAlbumBean);
			allFlacAlbumBeans.put(file, flacAlbumBean);
		}
		return flacAlbumBean;
	}

	protected FlacArtistBean findOrCreateArtist(String artist, File file) {
		Map<File, FlacArtistBean> allFlacArtistBeans = getAllFlacArtistBeans();
		FlacArtistBean flacArtistBean = allFlacArtistBeans.get(file);
		if (flacArtistBean == null) {
			flacArtistBean = new FlacArtistBean();
			setIdAndCode(flacArtistBean, artist);
			flacArtistBean.setFlacAlbumBeans(new TreeSet<FlacAlbumBean>());
			flacArtistBean.setRawName(artist.getBytes());
			allFlacArtistBeans.put(file, flacArtistBean);
		}
		return flacArtistBean;
	}

	protected void setIdAndCode(AbstractFlacBean<?> abstractFlacBean, String name) {
		abstractFlacBean.setCode(codeOf(name));
		abstractFlacBean.setId(nextId());
	}

	protected String codeOf(String name) {
		return name.toUpperCase();
	}

	public Predicate<AbstractFlacBean<?>> createCodedPredicate(String name) {
		final String code = codeOf(name);
		return new Predicate<AbstractFlacBean<?>>() {
			@Override
			public boolean evaluate(AbstractFlacBean<?> abstractFlacBean) {
				return code.equals(abstractFlacBean.getCode());
			}
		};
	}
	
	protected int nextId() {
		int nextId = getNextId();
		setNextId(nextId + 1);
		return nextId;
	}
	
	protected void removeAll(File dir) throws IOException {
		if (dir.exists()) {
			if (dir.isDirectory()) {
				FileUtils.deleteDirectory(dir);
			}
			else {
				dir.delete();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		removeAll(getWorkingDirectory());
	}

	public Map<File, FlacTrackBean> getAllFlacTrackBeans() {
		return i_allFlacTrackBeans;
	}

	public void setAllFlacTrackBeans(Map<File, FlacTrackBean> allFlacTrackBeans) {
		i_allFlacTrackBeans = allFlacTrackBeans;
	}

	public Map<File, FlacAlbumBean> getAllFlacAlbumBeans() {
		return i_allFlacAlbumBeans;
	}

	public void setAllFlacAlbumBeans(Map<File, FlacAlbumBean> allFlacAlbumBeans) {
		i_allFlacAlbumBeans = allFlacAlbumBeans;
	}

	public Map<File, FlacArtistBean> getAllFlacArtistBeans() {
		return i_allFlacArtistBeans;
	}

	public void setAllFlacArtistBeans(Map<File, FlacArtistBean> allFlacArtistBeans) {
		i_allFlacArtistBeans = allFlacArtistBeans;
	}

	public int getNextId() {
		return i_nextId;
	}

	public void setNextId(int nextId) {
		i_nextId = nextId;
	}
}
