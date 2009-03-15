package uk.co.unclealex.music.encoder.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.core.service.CommandWorker;

@Service
public class EncoderServiceImpl implements EncoderService {

	private static Logger log = Logger.getLogger(EncoderServiceImpl.class);

	private Integer i_maximumThreads = 8;
	
	private SingleEncoderService i_singleEncoderService;
	private SlimServerService i_slimServerService;
	private FlacTrackDao i_flacTrackDao;
	
	private AtomicBoolean i_atomicCurrentlyEncoding = new AtomicBoolean(false);
	private List<EncodingEventListener> i_encodingEventListeners = new ArrayList<EncodingEventListener>();

	@Override
	public void registerEncodingEventListener(
			EncodingEventListener encodingEventListener) {
		getEncodingEventListeners().add(encodingEventListener);
	}
	
	@Override
	public EncoderResultBean encodeAll()
	throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException {
		return encodeAll(getMaximumThreads());
	}
	
	public EncoderResultBean encodeAll(int maximumThreads)
	throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException {
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
		final SortedMap<String, File> commandCache = new TreeMap<String, File>();
		final BlockingQueue<EncodingCommandBean> encodingCommandBeans = new LinkedBlockingQueue<EncodingCommandBean>();
		List<CommandWorker<EncodingCommandBean>> workers = new ArrayList<CommandWorker<EncodingCommandBean>>(maximumThreads);
		final List<EncodedTrackBean> newEncodedTrackBeans = new Vector<EncodedTrackBean>();
		final String currentThreadName = Thread.currentThread().getName();
		for (int idx = 0; idx < maximumThreads; idx++) {
			final int threadIndex = idx;
			CommandWorker<EncodingCommandBean> worker = new CommandWorker<EncodingCommandBean>(encodingCommandBeans, errors) {
				@Override
				protected void process(EncodingCommandBean encodingCommandBean) throws IOException {
					Thread.currentThread().setName(currentThreadName + "-" + threadIndex);
					EncodedTrackBean encodedTrackBean =  
						singleEncoderService.encode(encodingCommandBean, commandCache);
					if (encodedTrackBean != null) {
						newEncodedTrackBeans.add(encodedTrackBean);
						FlacTrackBean flacTrackBean = getFlacTrackDao().findByUrl(encodingCommandBean.getUrl());
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
		singleEncoderService.populateCommandCache(commandCache);
		singleEncoderService.offerAll(encodingCommandBeans);
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
		singleEncoderService.updateAllFilenames();
		singleEncoderService.updateMissingAlbumInformation();
		singleEncoderService.updateOwnership();
		return new EncoderResultBean(flacAlbumBeans, totalCount);
	}

	@Override
	public EncoderResultBean encodeAllAndRemoveDeleted() throws AlreadyEncodingException,
			MultipleEncodingException, CurrentlyScanningException, IOException {
		int removeDeletedCount = getSingleEncoderService().removeDeleted(getEncodingEventListeners());
		EncoderResultBean encoderResultBean = encodeAll();
		return new EncoderResultBean(encoderResultBean.getFlacAlbumBeans(), encoderResultBean.getTracksAffected() + removeDeletedCount);
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

	public AtomicBoolean getAtomicCurrentlyEncoding() {
		return i_atomicCurrentlyEncoding;
	}

	public void setAtomicCurrentlyEncoding(AtomicBoolean atomicCurrentlyEncoding) {
		i_atomicCurrentlyEncoding = atomicCurrentlyEncoding;
	}

	public SingleEncoderService getSingleEncoderService() {
		return i_singleEncoderService;
	}

	@Required
	public void setSingleEncoderService(SingleEncoderService singleEncoderService) {
		i_singleEncoderService = singleEncoderService;
	}

	public List<EncodingEventListener> getEncodingEventListeners() {
		return i_encodingEventListeners;
	}

	public void setEncodingEventListeners(
			List<EncodingEventListener> encodingEventListeners) {
		i_encodingEventListeners = encodingEventListeners;
	}

	public SlimServerService getSlimServerService() {
		return i_slimServerService;
	}

	@Required
	public void setSlimServerService(SlimServerService slimServerService) {
		i_slimServerService = slimServerService;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	@Required
	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}
}
