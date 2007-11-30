package uk.co.unclealex.flacconverter.encoded.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.dao.EncoderDao;
import uk.co.unclealex.flacconverter.encoded.dao.TrackDataDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodingCommandBean;
import uk.co.unclealex.flacconverter.flac.dao.FlacTrackDao;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;
import uk.co.unclealex.flacconverter.flac.service.SlimServerService;

public class EncoderServiceImpl implements EncoderService {

	private static Logger log = Logger.getLogger(EncoderServiceImpl.class);

	private int i_maximumThreads = 1;
	
	private SlimServerService i_slimServerService;
	private EncodedTrackDao i_encodedTrackDao;
	private TrackDataDao i_trackDataDao;
	private EncoderDao i_encoderDao;
	private FlacTrackDao i_flacTrackDao;
	private SingleEncoderService i_singleEncoderService;
	
	private AtomicBoolean i_atomicCurrentlyEncoding = new AtomicBoolean(false);
	private Set<EncodingEventListener> i_encodingEventListeners = new HashSet<EncodingEventListener>();

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
		if (getSlimServerService().isScanning()) {
			throw new CurrentlyScanningException();
		}
		if (!getAtomicCurrentlyEncoding().compareAndSet(false, true)) {
			throw new AlreadyEncodingException();
		}
		final SingleEncoderService singleEncoderService = getSingleEncoderService();
		final SortedMap<EncodingCommandBean, Throwable> errors =
			Collections.synchronizedSortedMap(new TreeMap<EncodingCommandBean, Throwable>());
		final SortedMap<EncoderBean, File> commandCache = new TreeMap<EncoderBean, File>();
		final BlockingQueue<EncodingCommandBean> encodingCommandBeans = new LinkedBlockingQueue<EncodingCommandBean>();
		EncodingWorker[] workers = new EncodingWorker[maximumThreads];
		for (int idx = 0; idx < maximumThreads; idx++) {
			workers[idx] = new EncodingWorker(encodingCommandBeans, errors) {
				@Override
				protected void process(EncodingCommandBean encodingCommandBean) throws IOException {
					EncodedTrackBean encodedTrackBean =  
						singleEncoderService.encode(encodingCommandBean, commandCache);
					if (encodedTrackBean != null) {
						for (EncodingEventListener encodingEventListener : getEncodingEventListeners()) {
							encodingEventListener.afterTrackEncoded(encodedTrackBean, encodingCommandBean.getFlacTrackBean());
						}
					}
				}
			};
			workers[idx].start();
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
		for (EncodingWorker worker : workers) {
			encodingCommandBeans.offer(worker.getEndOfWorkBean());
		}
		int totalCount = 0;
		for (EncodingWorker worker : workers) {
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
		TrackDataDao trackDataDao = getTrackDataDao();
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
			for (int id : trackDataDao.getIdsForEncodedTrackBean(encodedTrackBean)) {
				trackDataDao.removeById(id);
			}
			encodedTrackDao.remove(encodedTrackBean);
			log.info(
					"Removed " + encodedTrackBean.getEncoderBean().getExtension() +
					" conversion of " + encodedTrackBean.getFlacUrl() + " " + encodedTrackBean.getId());
		}
		encodedTrackDao.flush();
		return extraTracks.size();
	}
	
	@Override
	public int encodeAllAndRemoveDeleted() throws AlreadyEncodingException,
			MultipleEncodingException, CurrentlyScanningException, IOException {
		return encodeAll() + removeDeleted();
	}
	
	public boolean isCurrentlyEncoding() {
		return getAtomicCurrentlyEncoding().get();
	}

	public int getMaximumThreads() {
		return i_maximumThreads;
	}

	public void setMaximumThreads(int maximumThreads) {
		i_maximumThreads = maximumThreads;
	}

	public SlimServerService getSlimServerService() {
		return i_slimServerService;
	}

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

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	public SingleEncoderService getSingleEncoderService() {
		return i_singleEncoderService;
	}

	public void setSingleEncoderService(SingleEncoderService singleEncoderService) {
		i_singleEncoderService = singleEncoderService;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public Set<EncodingEventListener> getEncodingEventListeners() {
		return i_encodingEventListeners;
	}

	public void setEncodingEventListeners(
			Set<EncodingEventListener> encodingEventListeners) {
		i_encodingEventListeners = encodingEventListeners;
	}

	public TrackDataDao getTrackDataDao() {
		return i_trackDataDao;
	}

	public void setTrackDataDao(TrackDataDao trackDataDao) {
		i_trackDataDao = trackDataDao;
	}

}
