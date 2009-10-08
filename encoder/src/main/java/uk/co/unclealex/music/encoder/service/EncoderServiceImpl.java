package uk.co.unclealex.music.encoder.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.EncodedService;
import uk.co.unclealex.music.core.service.CommandWorker;
import uk.co.unclealex.music.core.service.SlimServerService;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.exception.CurrentlyScanningException;
import uk.co.unclealex.music.encoder.exception.EncodingException;
import uk.co.unclealex.music.encoder.exception.MultipleEncodingException;

public class EncoderServiceImpl implements EncoderService {

	private static Logger log = Logger.getLogger(EncoderServiceImpl.class);

	private Integer i_maximumThreads = 8;
	private Lock i_lock;
	private TransactionalEncoderService i_transactionalEncoderService;
	private SlimServerService i_slimServerService;
	private FlacTrackDao i_flacTrackDao;
	private EncoderDao i_encoderDao;
	private EncodedService i_encodedService;
	
	@Override
	public List<EncodingAction> encodeAll() throws EncodingException {
		return encodeAll(getMaximumThreads());
	}
	
	public List<EncodingAction> encodeAll(int maximumThreads) throws EncodingException {
		if (getSlimServerService().isScanning()) {
			throw new CurrentlyScanningException();
		}
		return doEncodeAll(maximumThreads);
	}

	protected List<EncodingAction> doEncodeAll(int maximumThreads) throws MultipleEncodingException {
		log.info("Initiating encoding with " + maximumThreads + " threads.");
		final TransactionalEncoderService transactionalEncoderService = getTransactionalEncoderService();
		transactionalEncoderService.startEncoding();
		final List<EncodingAction> encodingActions = new LinkedList<EncodingAction>();
		final SortedMap<EncodingCommandBean, Throwable> errors =
			Collections.synchronizedSortedMap(new TreeMap<EncodingCommandBean, Throwable>());
		final BlockingQueue<EncodingCommandBean> encodingCommandBeans = new LinkedBlockingQueue<EncodingCommandBean>();
		List<CommandWorker<EncodingCommandBean>> workers = new ArrayList<CommandWorker<EncodingCommandBean>>(maximumThreads);
		final String currentThreadName = Thread.currentThread().getName();
		final FlacTrackDao flacTrackDao = getFlacTrackDao();
		final EncoderDao encoderDao = getEncoderDao();
		for (int idx = 0; idx < maximumThreads; idx++) {
			final int threadIndex = idx;
			CommandWorker<EncodingCommandBean> worker = new CommandWorker<EncodingCommandBean>(encodingCommandBeans, errors) {
				@Override
				protected void process(EncodingCommandBean encodingCommandBean) throws EncodingException {
					Thread.currentThread().setName(currentThreadName + "-" + threadIndex);
					FlacTrackBean flacTrackBean = flacTrackDao.findByUrl(encodingCommandBean.getUrl());
					EncoderBean encoderBean = encoderDao.findByExtension(encodingCommandBean.getExtension());
					// Make sure encoding actions are transactional insofar as they're only added if there is no exception.
					List<EncodingAction> currentEncodingActions = new LinkedList<EncodingAction>();
					transactionalEncoderService.encode(flacTrackBean, encoderBean, currentEncodingActions, getLock());
					encodingActions.addAll(currentEncodingActions);
				}
			};
			worker.start();
			workers.add(worker);
		}
		for (FlacTrackBean flacTrackBean : flacTrackDao.getAll()) {
			for (EncoderBean encoderBean : encoderDao.getAll()) {
				encodingCommandBeans.offer(new EncodingCommandBean(encoderBean.getExtension(), flacTrackBean.getUrl()));
			}
		}
		for (int idx = 0; idx < maximumThreads; idx++) {
			encodingCommandBeans.offer(new EncodingCommandBean());
		}
		for (CommandWorker<EncodingCommandBean> worker : workers) {
			try {
				worker.join();
			}
			catch (InterruptedException e) {
				// Do nothing
			}
		}
		log.info("Removing orphaned tracks.");
		doRemoveMissing(encodingActions);
		log.info("Updating ownership");
		try {
			transactionalEncoderService.updateOwnership(encodingActions);
		}
		catch (EncodingException e) {
			log.warn("Updating ownership failed.", e);
		}
		transactionalEncoderService.stopEncoding();
		if (!errors.isEmpty()) {
			throw new MultipleEncodingException(errors, encodingActions);
		}
		return encodingActions;
	}

	protected void doRemoveMissing(List<EncodingAction> encodingActions) {
		Set<EncodedTrackBean> orphanedEncodedTrackBeans = getEncodedService().findOrphanedEncodedTrackBeans();
		TransactionalEncoderService transactionalEncoderService = getTransactionalEncoderService();
		for (EncodedTrackBean orphanedEncodedTrackBean : orphanedEncodedTrackBeans) {
			try {
				List<EncodingAction> currentEncodingActions = new LinkedList<EncodingAction>();
				transactionalEncoderService.remove(orphanedEncodedTrackBean, currentEncodingActions, getLock());
				encodingActions.addAll(currentEncodingActions);
			}
			catch (EncodingException e) {
				log.error("Could not remove track " + orphanedEncodedTrackBean);
			}
		}
	}

	public Integer getMaximumThreads() {
		return i_maximumThreads;
	}

	public void setMaximumThreads(Integer maximumThreads) {
		i_maximumThreads = maximumThreads;
	}

	public TransactionalEncoderService getTransactionalEncoderService() {
		return i_transactionalEncoderService;
	}

	@Required
	public void setTransactionalEncoderService(TransactionalEncoderService transactionalEncoderService) {
		i_transactionalEncoderService = transactionalEncoderService;
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

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}

	public Lock getLock() {
		return i_lock;
	}

	public void setLock(Lock lock) {
		i_lock = lock;
	}
}
