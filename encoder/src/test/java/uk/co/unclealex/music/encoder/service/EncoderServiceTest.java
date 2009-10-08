package uk.co.unclealex.music.encoder.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;

import uk.co.unclealex.hibernate.dao.DataDao;
import uk.co.unclealex.music.base.dao.EncodedArtistDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.dao.FileDao;
import uk.co.unclealex.music.base.dao.FlacArtistDao;
import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.initialise.Initialiser;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
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
import uk.co.unclealex.music.test.TestFlacProvider;

public abstract class EncoderServiceTest extends EncoderSpringTest {

	public static final int SIMULTANEOUS_THREADS = 2;

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
	
	protected List<EncodingAction> doTestEncoding(String testName) throws EncodingException {
		List<EncodingAction> encodingActions = getEncoderService().encodeAll(SIMULTANEOUS_THREADS);
		checkArtists(testName);
		checkOwnership(testName);
		checkFilesystem(testName);
		checkData(testName);
		return encodingActions;
	}
	
	protected void doTestEncoding(String testName, List<EncodingAction> expectedEncodingActions) throws EncodingException {
		List<EncodingAction> actualEncodingActions = doTestEncoding(testName);
		assertEquals("The wrong encoding actions were returned for test " + testName, expectedEncodingActions, actualEncodingActions);
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
				String fullPath = titleFormatService.createTitle(encodedTrackBean, ownerBean);
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
	
	protected void checkData(String testName) {
		long trackCount = getEncodedTrackDao().count();
		long dataCount = getDataDao().count();
		assertEquals("The wrong number of data beans were found in test " + testName, trackCount, dataCount);
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
	
}
