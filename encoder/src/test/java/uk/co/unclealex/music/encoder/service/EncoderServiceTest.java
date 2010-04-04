package uk.co.unclealex.music.encoder.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.FunctorException;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;

import uk.co.unclealex.hibernate.dao.DataDao;
import uk.co.unclealex.hibernate.service.DataService;
import uk.co.unclealex.music.base.dao.AlbumCoverDao;
import uk.co.unclealex.music.base.dao.EncodedArtistDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.dao.FileDao;
import uk.co.unclealex.music.base.dao.FlacArtistDao;
import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.initialise.Initialiser;
import uk.co.unclealex.music.base.model.AlbumCoverBean;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncodedTrackFileBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.service.OwnerService;
import uk.co.unclealex.music.base.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.encoder.EncoderSpringTest;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.exception.EncodingException;
import uk.co.unclealex.music.encoder.listener.EncodingEventListener;
import uk.co.unclealex.music.fs.FileSystemMount;
import uk.co.unclealex.music.test.CanonicalAlbumCover;
import uk.co.unclealex.music.test.RawPictureData;
import uk.co.unclealex.music.test.TestFlacProvider;

public abstract class EncoderServiceTest extends EncoderSpringTest {

	private EncoderService i_encoderService;
	private TestFlacProvider i_testFlacProvider;
	private Initialiser i_initialiser;
	private FlacArtistDao i_flacArtistDao;
	private EncodedArtistDao i_encodedArtistDao;
	private EncoderDao i_encoderDao;
	private OwnerService i_ownerService;
	private OwnerDao i_ownerDao;
	private EncodedTrackDao i_encodedTrackDao;
	private DataDao i_dataDao;
	private TitleFormatService i_titleFormatService;
	private FileDao i_fileDao;
	private AlbumCoverDao i_albumCoverDao;
	private DataService i_dataService;
	private FileSystemMount i_fileSystemMount;
	private File i_mountPoint;
	private File i_dataStorageDirectory;
	
	private static boolean s_initialisationRequired = true;
	
	public EncoderServiceTest() {
		setDefaultRollback(false);
	}
	
	protected abstract String getTestBase();
	
	@Override
	protected void onSetUpBeforeTransaction() throws Exception {
		super.onSetUpBeforeTransaction();
		if (isInitialisationRequired()) {
			getInitialiser().initialise();
			getTestFlacProvider().initialise(getTestBase());
			setInitialisationRequired(false);
		}
	}
	
	protected List<EncodingAction> doTestEncoding(String testName, List<EncodingEventListener> encodingEventListeners) throws EncodingException, IOException {
		List<EncodingAction> encodingActions = getEncoderService().encodeAll(encodingEventListeners);
		checkArtists(testName);
		checkOwnership(testName);
		checkFilesystem(testName);
		checkCovers(testName);
		checkData(testName);
		checkFuseFilesystem(testName);
		return encodingActions;
	}
	
	protected void doTestEncodingAndCheckActions(String testName, List<EncodingAction> expectedEncodingActions) throws EncodingException, IOException {
		CalledEncodingEventListener calledEncodingEventListener = new CalledEncodingEventListener();
		List<EncodingAction> actualEncodingActions =
			doTestEncoding(testName, Collections.singletonList((EncodingEventListener) calledEncodingEventListener));
		assertEquals("The wrong encoding actions were returned for test " + testName, expectedEncodingActions, actualEncodingActions);
		assertTrue("The extra encoding event listener was not called.", calledEncodingEventListener.isCalled());
	}
	
	protected void checkArtists(String testName) {
		SortedMap<String, FlacArtistBean> allFlacArtistsByName = 
			makeSortedMap(
				getFlacArtistDao().getAll(), 
				new Transformer<FlacArtistBean, String>() {
					@Override
					public String transform(FlacArtistBean flacArtistBean) {
						return flacArtistBean.getName();
					}
				});
		SortedMap<String, EncodedArtistBean> allEncodedArtistsByName = 
			makeSortedMap(
				getEncodedArtistDao().getAll(), 
				new Transformer<EncodedArtistBean, String>() {
					@Override
					public String transform(EncodedArtistBean encodedArtistBean) {
						return encodedArtistBean.getName();
					}
				});
		assertEquals("The wrong artists were returned for test " + testName, allFlacArtistsByName.keySet(), allEncodedArtistsByName.keySet());
		for (Map.Entry<String, FlacArtistBean> entry : allFlacArtistsByName.entrySet()) {
			checkAlbums(testName, entry.getValue(), allEncodedArtistsByName.get(entry.getKey()));
		}
	}
	
	protected void checkAlbums(String testName, FlacArtistBean flacArtistBean, EncodedArtistBean encodedArtistBean) {
		SortedMap<String, FlacAlbumBean> allFlacAlbumsByTitle = 
			makeSortedMap(
				flacArtistBean.getFlacAlbumBeans(), 
				new Transformer<FlacAlbumBean, String>() {
					@Override
					public String transform(FlacAlbumBean flacAlbumBean) {
						return flacAlbumBean.getTitle();
					}
				});
		SortedMap<String, EncodedAlbumBean> allEncodedAlbumsByTitle = 
			makeSortedMap(
				encodedArtistBean.getEncodedAlbumBeans(), 
				new Transformer<EncodedAlbumBean, String>() {
					@Override
					public String transform(EncodedAlbumBean encodedArtistBean) {
						return encodedArtistBean.getTitle();
					}
				});
		assertEquals("The wrong albums were returned for artist " + flacArtistBean.getName() + " and test " + testName, 
				allFlacAlbumsByTitle.keySet(), allEncodedAlbumsByTitle.keySet());
		for (Map.Entry<String, FlacAlbumBean> entry : allFlacAlbumsByTitle.entrySet()) {
			checkTracks(testName, entry.getValue(), allEncodedAlbumsByTitle.get(entry.getKey()));
		}
	}

	protected void checkTracks(String testName, FlacAlbumBean flacAlbumBean, EncodedAlbumBean encodedAlbumBean) {
		SortedMap<String, FlacTrackBean> allFlacTracksByTitleAndEncoding = new TreeMap<String, FlacTrackBean>(); 
		for (final EncoderBean encoderBean : getEncoderDao().getAll()) {
			allFlacTracksByTitleAndEncoding.putAll(
				makeSortedMap(
						flacAlbumBean.getFlacTrackBeans(), 
						new Transformer<FlacTrackBean, String>() {
							@Override
							public String transform(FlacTrackBean flacTrackBean) {
								return flacTrackBean.getTrackNumber() + ". " + flacTrackBean.getTitle() + "." + encoderBean.getExtension();
							}
						}));			
		}
		SortedMap<String, EncodedTrackBean> allEncodedTracksByTitleAndEncoding = 
			makeSortedMap(
				encodedAlbumBean.getEncodedTrackBeans(), 
				new Transformer<EncodedTrackBean, String>() {
					@Override
					public String transform(EncodedTrackBean encodedTrackBean) {
						return encodedTrackBean.getTrackNumber() + ". " + encodedTrackBean.getTitle() + "." + encodedTrackBean.getEncoderBean().getExtension();
					}
				});
		assertEquals("The wrong tracks were returned for album " + flacAlbumBean.getTitle() + " and test " + testName, 
				allFlacTracksByTitleAndEncoding.keySet(), allEncodedTracksByTitleAndEncoding.keySet());
	}

	protected <E> SortedMap<String, E> makeSortedMap(Collection<E> values, Transformer<E, String> keyTransformer) {
		SortedMap<String, E> map = new TreeMap<String, E>();
		for (E value : values) {
			map.put(keyTransformer.transform(value), value);
		}
		return map;
	}
	
	protected void checkOwnership(String testName) {
		SortedMap<OwnerBean, SortedSet<FlacTrackBean>> flacOwnership = getOwnerService().resolveOwnershipByFiles();
		SortedMap<OwnerBean, SortedSet<EncodedTrackBean>> expectedOwnership = new TreeMap<OwnerBean, SortedSet<EncodedTrackBean>>();
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		for (Map.Entry<OwnerBean, SortedSet<FlacTrackBean>> entry : flacOwnership.entrySet()) {
			SortedSet<EncodedTrackBean> encodedTrackBeans = new TreeSet<EncodedTrackBean>();
			expectedOwnership.put(entry.getKey(), encodedTrackBeans);
			for (FlacTrackBean flacTrackBean : entry.getValue()) {
				encodedTrackBeans.addAll(encodedTrackDao.findByUrl(flacTrackBean.getUrl()));
			}
		}
		for (OwnerBean ownerBean : getOwnerDao().getAll()) {
			assertEquals(
					"Owner " + ownerBean.getName() + " owned the wrong tracks in test " + testName, 
					expectedOwnership.get(ownerBean), encodedTrackDao.findByOwnerBean(ownerBean));
		}
	}
	
	protected void checkFilesystem(String testName) {
		SortedSet<String> actualPaths =	new TreeSet<String>(getFileDao().findAllPaths());
		SortedSet<String> expectedPaths = createExpectedPaths();
		assertEquals("The wrong paths were returned in test " + testName, expectedPaths, actualPaths);
	}
	
	protected SortedSet<String> createExpectedPaths() {
		SortedSet<String> expectedPaths = new TreeSet<String>();
		TitleFormatService titleFormatService = getTitleFormatService();
		for (EncodedTrackBean encodedTrackBean : getEncodedTrackDao().getAll()) {
			for (OwnerBean ownerBean : encodedTrackBean.getOwnerBeans()) {
				String fullPath = titleFormatService.createTitle(encodedTrackBean, ownerBean, true);
				String[] pathParts = StringUtils.split(fullPath, '/');
				for (int idx = 0; idx < pathParts.length; idx++) {
					String path = StringUtils.join(Arrays.copyOf(pathParts, idx), '/');
					expectedPaths.add(path);
				}
				expectedPaths.add(fullPath);
			}
		}
		return expectedPaths;
	}
	
	protected void checkCovers(String testName) throws IOException {
		Transformer<AlbumCoverBean, CanonicalAlbumCover> transformer = new Transformer<AlbumCoverBean, CanonicalAlbumCover>() {
			@Override
			public CanonicalAlbumCover transform(AlbumCoverBean albumCoverBean) {
				try {
					URI uri = new URI(albumCoverBean.getUrl());
					File file = new File(uri);
					BufferedImage image = ImageIO.read(file);
					long size = (long) image.getWidth() * image.getHeight();
					return new CanonicalAlbumCover(
							file,
							size, 
							albumCoverBean.getArtistCode(), 
							albumCoverBean.getAlbumCode(), 
							albumCoverBean.getDateSelected() != null);
				}
				catch (IOException e) {
					throw new FunctorException("Cannot read image " + albumCoverBean.getUrl());
				}
				catch (URISyntaxException e) {
					throw new FunctorException("Cannot read image " + albumCoverBean.getUrl());
				}
			}
		};
		Set<CanonicalAlbumCover> canonicalAlbumCovers = getTestFlacProvider().getCanonicalAlbumCovers();
		assertEquals(
			"The wrong album cover information was returned for test " + testName, 
			canonicalAlbumCovers,
			CollectionUtils.collect(getAlbumCoverDao().getAll(), transformer));
		for (FlacTrackBean flacTrackBean : getTestFlacProvider().getAllFlacTrackBeans().values()) {
			FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
			checkPicturesEqual(
					flacAlbumBean.getFlacArtistBean().getCode(), flacAlbumBean.getCode(), flacTrackBean.getTitle(), flacTrackBean.getFile(), testName);
		}
		DataService dataService = getDataService();
		for (EncodedTrackBean encodedTrackBean : getEncodedTrackDao().getAll()) {
			EncodedAlbumBean encodedAlbumBean = encodedTrackBean.getEncodedAlbumBean();
			checkPicturesEqual(
					encodedAlbumBean.getEncodedArtistBean().getCode(), encodedAlbumBean.getCode(), 
					encodedTrackBean.getTitle(), dataService.findFile(encodedTrackBean.getTrackDataBean()), testName);
		}
	}
	
	protected void checkPicturesEqual(final String artistCode, final String albumCode, String title, File dataFile, String testName) throws IOException {
		CanonicalAlbumCover canonicalAlbumCover =
			CollectionUtils.find(
				getTestFlacProvider().getCanonicalAlbumCovers(),
				new Predicate<CanonicalAlbumCover>() {
					@Override
					public boolean evaluate(CanonicalAlbumCover canonicalAlbumCover) {
						return 
							artistCode.equals(canonicalAlbumCover.getArtistCode()) && 
							albumCode.equals(canonicalAlbumCover.getAlbumCode()) &&
							canonicalAlbumCover.isSelected();
					}
				});
		RawPictureData expectedData = canonicalAlbumCover==null?null:canonicalAlbumCover.getRawPictureData();
		String trackName = String.format("%s: %s, %s.%s", artistCode, albumCode, title, FilenameUtils.getExtension(dataFile.getName()));
		Tag tag;
		try {
			tag = AudioFileIO.read(dataFile).getTag();
		}
		catch (Exception e) {
			throw new RuntimeException("Could not read tag data for " + trackName + " in test " + testName, e);
		}
		List<Artwork> artworkList = tag.getArtworkList();
		if (artworkList.isEmpty()) {
			if (expectedData != null) {
				fail("Artwork was not found in file " + trackName + " when some was expected in test " + testName);
			}
		}
		else {
			assertEquals("The wrong amount of artwork was found in file " + trackName + " in test " + testName, 1, artworkList.size());
			if (expectedData == null) {
				fail("Artwork was found in file " + trackName + " when none was expected in test " + testName);
			}
			RawPictureData artworkData = new RawPictureData(artworkList.get(0).getBinaryData());
			assertEquals(
					"The artwork in file " + trackName + " was not correct in test " + testName, 
					expectedData, artworkData);
		}
	}

	protected void checkData(String testName) {
		long trackAndCoverCount = getEncodedTrackDao().count() + getAlbumCoverDao().count();
		long dataCount = getDataDao().count();
		assertEquals("The wrong number of data beans were found in test " + testName, trackAndCoverCount, dataCount);
	}
	
	protected void checkFuseFilesystem(String testName) throws IOException {
		File mountPoint = getMountPoint();
		mountPoint.mkdirs();
		FileSystemMount fileSystemMount = getFileSystemMount();
		fileSystemMount.mount();
		try {
			SortedSet<String> expectedFileNames = new TreeSet<String>(getFileDao().findAllPaths());
			SortedSet<String> actualFileNames = new TreeSet<String>();
			listFiles(mountPoint, "", actualFileNames);
			actualFileNames.add("");
			assertEquals("The wrong files were mounted.", expectedFileNames, actualFileNames);
			File dataStorageDirectory = getDataStorageDirectory();
			for (EncodedTrackFileBean encodedTrackFileBean : getFileDao().getAllNormalFiles()) {
				File expectedFile = 
					new File(
						dataStorageDirectory,
						encodedTrackFileBean.getEncodedTrackBean().getTrackDataBean().getFilename());
				String path = encodedTrackFileBean.getPath();
				File actualFile = 
					new File(mountPoint, path);
				InputStream expectedIn = new FileInputStream(expectedFile);
				InputStream actualIn = new FileInputStream(actualFile);
				byte[] expectedData = IOUtils.toByteArray(expectedIn);
				byte[] actualData = IOUtils.toByteArray(actualIn);
				assertTrue("The wrong data was returned for path " + path, Arrays.equals(expectedData, actualData));
			}
		}
		finally {
			fileSystemMount.unmount();
			mountPoint.delete();
		}
	}
	
	protected void listFiles(File directory, String directoryName, SortedSet<String> fileNames) {
		if (!directory.isDirectory()) {
			return;
		}
		for (File file : directory.listFiles()) {
			String fullName = directoryName + file.getName();
			fileNames.add(fullName);
			if (file.isDirectory()) {
				listFiles(file, fullName + "/", fileNames);
			}
		}
	}

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

	public TestFlacProvider getTestFlacProvider() {
		return i_testFlacProvider;
	}

	public void setTestFlacProvider(TestFlacProvider testFlacProvider) {
		i_testFlacProvider = testFlacProvider;
	}

	public Initialiser getInitialiser() {
		return i_initialiser;
	}

	public void setInitialiser(Initialiser initialiser) {
		i_initialiser = initialiser;
	}

	public FlacArtistDao getFlacArtistDao() {
		return i_flacArtistDao;
	}

	public void setFlacArtistDao(FlacArtistDao flacArtistDao) {
		i_flacArtistDao = flacArtistDao;
	}

	public EncodedArtistDao getEncodedArtistDao() {
		return i_encodedArtistDao;
	}

	public void setEncodedArtistDao(EncodedArtistDao encodedArtistDao) {
		i_encodedArtistDao = encodedArtistDao;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public TitleFormatService getTitleFormatService() {
		return i_titleFormatService;
	}

	public void setTitleFormatService(TitleFormatService titleFormatService) {
		i_titleFormatService = titleFormatService;
	}

	public FileDao getFileDao() {
		return i_fileDao;
	}

	public void setFileDao(FileDao fileDao) {
		i_fileDao = fileDao;
	}

	public boolean isInitialisationRequired() {
		return s_initialisationRequired;
	}

	public void setInitialisationRequired(boolean initialisationRequired) {
		s_initialisationRequired = initialisationRequired;
	}

	public DataDao getDataDao() {
		return i_dataDao;
	}

	public void setDataDao(DataDao dataDao) {
		i_dataDao = dataDao;
	}

	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}

	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}

	public DataService getDataService() {
		return i_dataService;
	}

	public void setDataService(DataService dataService) {
		i_dataService = dataService;
	}

	public FileSystemMount getFileSystemMount() {
		return i_fileSystemMount;
	}

	public void setFileSystemMount(FileSystemMount fileSystemMount) {
		i_fileSystemMount = fileSystemMount;
	}

	public File getMountPoint() {
		return i_mountPoint;
	}

	public void setMountPoint(File mountPoint) {
		i_mountPoint = mountPoint;
	}

	public File getDataStorageDirectory() {
		return i_dataStorageDirectory;
	}

	public void setDataStorageDirectory(File dataStorageDirectory) {
		i_dataStorageDirectory = dataStorageDirectory;
	}
	
}
