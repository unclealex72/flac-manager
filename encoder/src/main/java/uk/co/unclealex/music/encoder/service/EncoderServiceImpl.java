package uk.co.unclealex.music.encoder.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DelegateFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;
import uk.co.unclealex.music.base.dao.EncodedAlbumDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.EncodedService;
import uk.co.unclealex.music.base.service.FlacService;
import uk.co.unclealex.music.base.service.OwnerService;
import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;
import uk.co.unclealex.music.core.service.CommandWorker;

@Service
@Transactional
public class EncoderServiceImpl implements EncoderService {

	private static Logger log = Logger.getLogger(EncoderServiceImpl.class);

	private Integer i_maximumThreads = 8;
	
	private SlimServerService i_slimServerService;
	private EncodedTrackDao i_encodedTrackDao;
	private EncodedAlbumDao i_encodedAlbumDao;
	private EncoderDao i_encoderDao;
	private EncodedService i_encodedService;
	private FlacTrackDao i_flacTrackDao;
	private SingleEncoderService i_singleEncoderService;
	private FlacService i_flacService;
	private OwnerService i_ownerService;
	private AlbumCoverService i_albumCoverService;
	private RepositoryManager i_encodedRepositoryManager;
	
	private AtomicBoolean i_atomicCurrentlyEncoding = new AtomicBoolean(false);
	private List<EncodingEventListener> i_encodingEventListeners = new ArrayList<EncodingEventListener>();

	@Override
	public void registerEncodingEventListener(
			EncodingEventListener encodingEventListener) {
		getEncodingEventListeners().add(encodingEventListener);
	}
	
	@Override
	public int encodeAll()
	throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException {
		return encodeAll(getMaximumThreads());
	}
	
	public int encodeAll(int maximumThreads)
	throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException {
		AlbumCoverService albumCoverService = getAlbumCoverService();
		if (getSlimServerService().isScanning()) {
			throw new CurrentlyScanningException();
		}
		if (!getAtomicCurrentlyEncoding().compareAndSet(false, true)) {
			throw new AlreadyEncodingException();
		}
		log.info("Initiating encoding with " + maximumThreads + " threads.");
		final Set<FlacAlbumBean> flacAlbumBeans = new TreeSet<FlacAlbumBean>();
		final SingleEncoderService singleEncoderService = getSingleEncoderService();
		final SortedMap<EncodingCommandBean, Throwable> errors =
			Collections.synchronizedSortedMap(new TreeMap<EncodingCommandBean, Throwable>());
		final SortedMap<EncoderBean, File> commandCache = new TreeMap<EncoderBean, File>();
		final BlockingQueue<EncodingCommandBean> encodingCommandBeans = new LinkedBlockingQueue<EncodingCommandBean>();
		List<CommandWorker<EncodingCommandBean>> workers = new ArrayList<CommandWorker<EncodingCommandBean>>(maximumThreads);
		final List<EncodedTrackBean> newEncodedTrackBeans = new Vector<EncodedTrackBean>();
		for (int idx = 0; idx < maximumThreads; idx++) {
			CommandWorker<EncodingCommandBean> worker = new CommandWorker<EncodingCommandBean>(encodingCommandBeans, errors) {
				@Override
				protected void process(EncodingCommandBean encodingCommandBean) throws IOException {
					EncodedTrackBean encodedTrackBean =  
						singleEncoderService.encode(encodingCommandBean, commandCache);
					if (encodedTrackBean != null) {
						newEncodedTrackBeans.add(encodedTrackBean);
						FlacTrackBean flacTrackBean = encodingCommandBean.getFlacTrackBean();
						flacAlbumBeans.add(flacTrackBean.getFlacAlbumBean());
						for (EncodingEventListener encodingEventListener : getEncodingEventListeners()) {
							encodingEventListener.afterTrackEncoded(encodedTrackBean, flacTrackBean);
						}
					}
				}
			};
			worker.start();
			workers.add(worker);
		}
		SortedSet<EncoderBean> allEncoderBeans = getEncoderDao().getAll();
		for (EncoderBean encoderBean : allEncoderBeans) {
			commandCache.put(encoderBean, singleEncoderService.createCommandFile(encoderBean));
		}
		for (FlacTrackBean flacTrackBean : getFlacTrackDao().getAll()) {
			for (EncoderBean encoderBean : allEncoderBeans) {
				encodingCommandBeans.offer(new EncodingCommandBean(encoderBean, flacTrackBean));
			}
		}
		for (int idx = 0; idx < maximumThreads; idx++) {
			encodingCommandBeans.offer(new EncodingCommandBean());
		}
		int totalCount = 0;
		for (CommandWorker<EncodingCommandBean> worker : workers) {
			try {
				worker.join();
			}
			catch (InterruptedException e) {
				// Do nothing
			}
			totalCount += worker.getCount();
		}
		
		for (File command : commandCache.values()) {
			command.delete();
		}
		getAtomicCurrentlyEncoding().set(false);
		if (!errors.isEmpty()) {
			throw new MultipleEncodingException(errors, totalCount);
		}
		getEncodedService().updateAllFilenames();
		updateMissingAlbumInformation();
		updateOwnership();
		
		RepositoryManager repositoryManager = getEncodedRepositoryManager();
		repositoryManager.clear();
		repositoryManager.refresh();
		albumCoverService.downloadAndSaveCoversForAlbums(flacAlbumBeans);
		albumCoverService.purgeCovers();
		return totalCount;
	}

	public int removeDeleted() {
		final SortedSet<String> urls = new TreeSet<String>();
		CollectionUtils.collect(
			getFlacTrackDao().getAll(),
			new Transformer<FlacTrackBean, String>() {
				public String transform(FlacTrackBean flacTrackBean) {
					return flacTrackBean.getUrl();
				}
			},
			urls);
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		SortedSet<EncodedTrackBean> extraTracks = new TreeSet<EncodedTrackBean>();
		extraTracks.addAll(
			CollectionUtils.selectRejected(
				encodedTrackDao.getAll(),
				new Predicate<EncodedTrackBean>() {
					public boolean evaluate(EncodedTrackBean encodedTrackBean) {
						return urls.contains(encodedTrackBean.getFlacUrl());
					}
				}));
		Collection<EncodingEventListener> encodingEventListeners = getEncodingEventListeners(); 
		for (EncodedTrackBean encodedTrackBean : extraTracks) {
			for (EncodingEventListener encodingEventListener : encodingEventListeners) {
				encodingEventListener.beforeTrackRemoved(encodedTrackBean);
			}
			encodedTrackDao.remove(encodedTrackBean);
			log.info(
					"Removed " + encodedTrackBean.getEncoderBean().getExtension() +
					" conversion of " + encodedTrackBean.getFlacUrl() + " " + encodedTrackBean.getId());
		}
		encodedTrackDao.flush();
		getEncodedService().removeEmptyAlbumsAndArtists();
		return extraTracks.size();
	}
	
	@Override
	public int encodeAllAndRemoveDeleted() throws AlreadyEncodingException,
			MultipleEncodingException, CurrentlyScanningException, IOException {
		int changeCount = removeDeleted() + encodeAll();
		log.info("There were " + changeCount + " changes.");
		return changeCount;
	}
	
	@Override
	public void updateMissingAlbumInformation() {
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		FlacTrackDao flacTrackDao = getFlacTrackDao();
		FlacService flacService = getFlacService();
		for (EncodedTrackBean encodedTrackBean : encodedTrackDao.findTracksWithoutAnAlbum()) {
			String flacUrl = encodedTrackBean.getFlacUrl();
			FlacAlbumBean flacAlbumBean = flacTrackDao.findByUrl(flacUrl).getFlacAlbumBean();
			EncodedAlbumBean encodedAlbumBean = flacService.findOrCreateEncodedAlbumBean(flacAlbumBean);
			encodedTrackBean.setEncodedAlbumBean(encodedAlbumBean);
			encodedTrackDao.store(encodedTrackBean);
			log.info("Updated album information for " + flacUrl);
		}
	}
	
	@Override
	public void updateOwnership() {
		final Map<String, SortedSet<EncodedAlbumBean>> albumsByOwner = new HashMap<String, SortedSet<EncodedAlbumBean>>();
		final Map<String, SortedSet<EncodedArtistBean>> artistsByOwner = new HashMap<String, SortedSet<EncodedArtistBean>>();
		final FlacService flacService = getFlacService();
		String rootDirectory = StringUtils.removeStart(getFlacService().getRootUrl(), "file://");
		final int rootDepth = StringUtils.split(rootDirectory, "/").length;
		final Pattern pattern = Pattern.compile("owner\\.(.+)");
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				String path = file.getAbsolutePath();
				String name = FilenameUtils.getName(path);
				String dir = FilenameUtils.getFullPath(path);
				Matcher matcher = pattern.matcher(name);
				if (matcher.matches()) {
					String owner = matcher.group(1);
					int depth = StringUtils.split(dir, "/").length - rootDepth;
					FlacAlbumBean flacAlbumBean = getFlacService().findFlacAlbumByPath(dir);
					if (flacAlbumBean != null) {
						EncodedAlbumBean encodedAlbumBean = flacService.findOrCreateEncodedAlbumBean(flacAlbumBean);
						if (depth == 1) {
							add(owner, artistsByOwner, encodedAlbumBean.getEncodedArtistBean(), new TreeSet<EncodedArtistBean>());
						}
						else if (depth == 2) {
							add(owner, albumsByOwner, encodedAlbumBean, new TreeSet<EncodedAlbumBean>());
						}
					}
				}
				return false; 
			}
			
			protected <E extends Comparable<E>> void add(
					String owner, Map<String, SortedSet<E>> map, E element, SortedSet<E> empty) {
				SortedSet<E> existing = map.get(owner);
				if (existing == null) {
					map.put(owner, empty);
					existing = empty;
				}
				existing.add(element);
			}
		};
		FileUtils.listFiles(new File(rootDirectory), new DelegateFileFilter(filter), TrueFileFilter.INSTANCE);
		SortedSet<String> owners = new TreeSet<String>(albumsByOwner.keySet());
		owners.addAll(artistsByOwner.keySet());
		for (String owner : owners) {
			getOwnerService().updateOwnership(owner, artistsByOwner.get(owner), albumsByOwner.get(owner));
		}
	}
	
	public boolean isCurrentlyEncoding() {
		return getAtomicCurrentlyEncoding().get();
	}

	public Integer getMaximumThreads() {
		return i_maximumThreads;
	}

	public void setMaximumThreads(Integer maximumThreads) {
		i_maximumThreads = maximumThreads;
	}

	public SlimServerService getSlimServerService() {
		return i_slimServerService;
	}

	@Required
	public void setSlimServerService(SlimServerService slimServerService) {
		i_slimServerService = slimServerService;
	}

	public AtomicBoolean getAtomicCurrentlyEncoding() {
		return i_atomicCurrentlyEncoding;
	}

	public void setAtomicCurrentlyEncoding(AtomicBoolean atomicCurrentlyEncoding) {
		i_atomicCurrentlyEncoding = atomicCurrentlyEncoding;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	@Required
	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	@Required
	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	public SingleEncoderService getSingleEncoderService() {
		return i_singleEncoderService;
	}

	@Required
	public void setSingleEncoderService(SingleEncoderService singleEncoderService) {
		i_singleEncoderService = singleEncoderService;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public List<EncodingEventListener> getEncodingEventListeners() {
		return i_encodingEventListeners;
	}

	public void setEncodingEventListeners(
			List<EncodingEventListener> encodingEventListeners) {
		i_encodingEventListeners = encodingEventListeners;
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	@Required
	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}

	public FlacService getFlacService() {
		return i_flacService;
	}

	@Required
	public void setFlacService(FlacService flacService) {
		i_flacService = flacService;
	}

	public EncodedAlbumDao getEncodedAlbumDao() {
		return i_encodedAlbumDao;
	}

	@Required
	public void setEncodedAlbumDao(EncodedAlbumDao encodedAlbumDao) {
		i_encodedAlbumDao = encodedAlbumDao;
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	@Required
	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}

	public AlbumCoverService getAlbumCoverService() {
		return i_albumCoverService;
	}

	@Required
	public void setAlbumCoverService(AlbumCoverService albumCoverService) {
		i_albumCoverService = albumCoverService;
	}

	public RepositoryManager getEncodedRepositoryManager() {
		return i_encodedRepositoryManager;
	}

	@Required
	public void setEncodedRepositoryManager(
			RepositoryManager encodedRepositoryManager) {
		i_encodedRepositoryManager = encodedRepositoryManager;
	}

}
