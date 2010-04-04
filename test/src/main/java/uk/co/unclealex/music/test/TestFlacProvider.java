package uk.co.unclealex.music.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.FunctorException;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;

import uk.co.unclealex.music.base.model.AbstractFlacBean;
import uk.co.unclealex.music.base.model.ExternalCoverArtImage;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.EncoderConfiguration;
import uk.co.unclealex.music.base.service.ExternalCoverArtException;
import uk.co.unclealex.music.base.service.ExternalCoverArtService;

public class TestFlacProvider implements ExternalCoverArtService, EncoderConfiguration {

	private Map<File, FlacTrackBean> i_allFlacTrackBeans;
	private Map<File, FlacAlbumBean> i_allFlacAlbumBeans;
	private Map<File, FlacArtistBean> i_allFlacArtistBeans;
	private Set<ExternalCoverArtImage> i_externalCoverArtImages;
	private Set<CanonicalAlbumCover> i_canonicalAlbumCovers;
	private int i_threadCount;
	private File i_importDirectory;
	
	private int i_nextId;
	
	public File getWorkingDirectory() {
		return new File(new File(System.getProperty("java.io.tmpdir")), "testFlacProvider");
	}
	
	public void initialise(String testBase) throws IOException {
		File resourceDirectory = findResourceDirectory(testBase);
		setImportDirectory(findImportDirectory(testBase));
		File workingDirectory = getWorkingDirectory();
		removeAll(workingDirectory);
		FileUtils.copyDirectory(resourceDirectory, workingDirectory);
		update();
	}

	protected File findResourceDirectory(String testBase) throws IOException {
		String type = "testdata";
		return findResourceDirectory(type, testBase);
	}

	protected File findImportDirectory(String testBase) throws IOException {
		String type = "import";
		return findResourceDirectory(type, testBase);
	}

	protected File findResourceDirectory(String type, String testBase) throws FileNotFoundException, IOException {
		String resourceName = type + "/" + testBase + "/ident.txt";
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
		setImportDirectory(findImportDirectory(testBase));
		FileUtils.copyDirectory(resourceDirectory, getWorkingDirectory());
		update();		
	}
	
	protected void update() {
		setNextId(0);
		setAllFlacAlbumBeans(new HashMap<File, FlacAlbumBean>());
		setAllFlacArtistBeans(new HashMap<File, FlacArtistBean>());
		setAllFlacTrackBeans(new HashMap<File, FlacTrackBean>());
		setExternalCoverArtImages(new TreeSet<ExternalCoverArtImage>());
		setCanonicalAlbumCovers(new HashSet<CanonicalAlbumCover>());
		FileUtils.listFiles(
				getWorkingDirectory(),
				FileFilterUtils.asFileFilter(new FlacOrImageFileFilter()), 
				FileFilterUtils.trueFileFilter());
	}
	
	protected class FlacOrImageFileFilter implements FileFilter {
		@Override
		public boolean accept(File file) {
			String ext = FilenameUtils.getExtension(file.getName());
			if ("flac".equalsIgnoreCase(ext)) {
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
			else if ("gif".equalsIgnoreCase(ext) || "jpg".equalsIgnoreCase(ext)) {
				Pattern artistAlbumPattern = Pattern.compile("(.+) - (.+) \\d+x\\d+");
				Matcher matcher = artistAlbumPattern.matcher(FilenameUtils.getBaseName(file.getPath()));
				if (matcher.matches()) {
					try {
						BufferedImage image = ImageIO.read(file);
						int size = image.getWidth() * image.getHeight();
						URL url = file.toURI().toURL();
						getExternalCoverArtImages().add(new ExternalCoverArtImage(url, size));
						getCanonicalAlbumCovers().add(
								new CanonicalAlbumCover(
										file, size, matcher.group(1).toUpperCase(), matcher.group(2).toUpperCase(), size == 4));
					}
					catch (IOException e) {
						throw new IllegalArgumentException("Could not read image file " + file);
					}
				}
			}
			return false;
		}
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
	public SortedSet<ExternalCoverArtImage> searchForImages(String artist, String album)
			throws ExternalCoverArtException, IOException {
		SortedSet<ExternalCoverArtImage> images = new TreeSet<ExternalCoverArtImage>();
		final String suffix = artist + " - " + album;
		Predicate<ExternalCoverArtImage> predicate = new Predicate<ExternalCoverArtImage>() {
			@Override
			public boolean evaluate(ExternalCoverArtImage externalCoverArtImage) {
				try {
					String path = new File(externalCoverArtImage.getUrl().toURI()).getPath();
					return FilenameUtils.getBaseName(path).startsWith(suffix);
				}
				catch (URISyntaxException e) {
					throw new FunctorException(e);
				}
			}
		};
		CollectionUtils.select(getExternalCoverArtImages(), predicate, images);
		return images;
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

	public Set<ExternalCoverArtImage> getExternalCoverArtImages() {
		return i_externalCoverArtImages;
	}

	public void setExternalCoverArtImages(Set<ExternalCoverArtImage> externalCoverArtImages) {
		i_externalCoverArtImages = externalCoverArtImages;
	}

	public Set<CanonicalAlbumCover> getCanonicalAlbumCovers() {
		return i_canonicalAlbumCovers;
	}

	public void setCanonicalAlbumCovers(Set<CanonicalAlbumCover> canonicalAlbumCovers) {
		i_canonicalAlbumCovers = canonicalAlbumCovers;
	}

	public int getThreadCount() {
		return i_threadCount;
	}

	public void setThreadCount(int threadCount) {
		i_threadCount = threadCount;
	}

	public File getImportDirectory() {
		return i_importDirectory;
	}

	public void setImportDirectory(File importDirectory) {
		i_importDirectory = importDirectory;
	}
}
