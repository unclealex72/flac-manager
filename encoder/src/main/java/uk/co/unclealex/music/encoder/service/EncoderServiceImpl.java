package uk.co.unclealex.music.encoder.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.base.RequiresPackages;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.service.EncodedService;
import uk.co.unclealex.music.base.service.EncoderConfiguration;
import uk.co.unclealex.music.base.service.OwnerService;
import uk.co.unclealex.music.core.service.SlimServerService;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.exception.CurrentlyScanningException;
import uk.co.unclealex.music.encoder.exception.EncodingException;
import uk.co.unclealex.music.encoder.exception.EventException;
import uk.co.unclealex.music.encoder.exception.MultipleEncodingException;
import uk.co.unclealex.music.encoder.listener.EncodingEventListener;
import uk.co.unclealex.music.encoding.service.TransactionalEncoderService;

public class EncoderServiceImpl implements EncoderService, RequiresPackages {

	private static Logger log = Logger.getLogger(EncoderServiceImpl.class);

	private EncoderConfiguration i_encoderConfiguration;
	private TransactionalEncoderService i_transactionalEncoderService;
	private SlimServerService i_slimServerService;
	private FlacTrackDao i_flacTrackDao;
	private EncoderDao i_encoderDao;
	private EncodedService i_encodedService;
	private OwnerDao i_ownerDao;
	private OwnerService i_ownerService;
	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	public List<EncodingAction> encodeAll() throws EncodingException {
		return encodeAll(new ArrayList<EncodingEventListener>());
	}
	
	public List<EncodingAction> encodeAll(List<EncodingEventListener> encodingEventListeners) throws EncodingException {
		if (getSlimServerService().isScanning()) {
			throw new CurrentlyScanningException();
		}
		return doEncodeAll(encodingEventListeners);
	}

	protected List<EncodingAction> doEncodeAll(final List<EncodingEventListener> encodingEventListeners) throws MultipleEncodingException {
		log.info("Initiating encoding.");
		TransactionalEncoderService transactionalEncoderService = getTransactionalEncoderService();
		transactionalEncoderService.startEncoding(encodingEventListeners);
		List<EncodingAction> encodingActions = new LinkedList<EncodingAction>();
		SortedMap<EncodingCommandBean, Throwable> errors =
			Collections.synchronizedSortedMap(new TreeMap<EncodingCommandBean, Throwable>());
		FlacTrackDao flacTrackDao = getFlacTrackDao();
		EncoderDao encoderDao = getEncoderDao();
		for (FlacTrackBean flacTrackBean : flacTrackDao.getAll()) {
			for (EncoderBean encoderBean : encoderDao.getAll()) {
				// Make sure encoding actions are transactional insofar as they're only added if there is no exception.
				List<EncodingAction> currentEncodingActions = new LinkedList<EncodingAction>();
				try {
					transactionalEncoderService.encode(flacTrackBean, encoderBean, currentEncodingActions, encodingEventListeners);
					encodingActions.addAll(currentEncodingActions);
				}
				catch (EncodingException e) {
					String url = flacTrackBean.getUrl();
					String extension = encoderBean.getExtension();
					log.error("Encoding " + url + " to " + extension + " failed.", e);
					errors.put(new EncodingCommandBean(extension, url), e);
				}
			}
		}
		log.info("Removing orphaned tracks.");
		doRemoveMissing(encodingActions, encodingEventListeners);
		log.info("Updating ownership");
		try {
			updateOwnership(encodingActions, encodingEventListeners);
		}
		catch (EncodingException e) {
			log.warn("Updating ownership failed.", e);
		}
		transactionalEncoderService.stopEncoding(encodingEventListeners);
		if (!errors.isEmpty()) {
			throw new MultipleEncodingException(errors, encodingActions);
		}
		return encodingActions;
	}

	public void updateOwnership(List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EventException {
		SortedMap<OwnerBean, SortedSet<EncodedTrackBean>> requiredOwnership = new TreeMap<OwnerBean, SortedSet<EncodedTrackBean>>(); 
		SortedSet<EncodedTrackBean> allEncodedTrackBeans = getEncodedTrackDao().getAllWithOwners();
		for (Map.Entry<OwnerBean, SortedSet<FlacTrackBean>> entry : getOwnerService().resolveOwnershipByFiles().entrySet()) {
			SortedSet<EncodedTrackBean> encodedTrackBeans = new TreeSet<EncodedTrackBean>();
			for (FlacTrackBean flacTrackBean : entry.getValue()) {
				final String url = flacTrackBean.getUrl();
				Predicate<EncodedTrackBean> predicate = new Predicate<EncodedTrackBean>() {
					@Override
					public boolean evaluate(EncodedTrackBean encodedTrackBean) {
						return url.equals(encodedTrackBean.getFlacUrl());
					}
				};
				encodedTrackBeans.addAll(CollectionUtils.select(allEncodedTrackBeans, predicate));
			}
			requiredOwnership.put(entry.getKey(), encodedTrackBeans);
		}
		// Calculate deltas for each owner.
		for (final OwnerBean ownerBean : getOwnerDao().getAll()) {
			Predicate<EncodedTrackBean> predicate = new Predicate<EncodedTrackBean>() {
				@Override
				public boolean evaluate(EncodedTrackBean encodedTrackBean) {
					return encodedTrackBean.getOwnerBeans().contains(ownerBean);
				}
			};
			SortedSet<EncodedTrackBean> required = requiredOwnership.get(ownerBean);
			SortedSet<EncodedTrackBean> current = new TreeSet<EncodedTrackBean>(CollectionUtils.select(allEncodedTrackBeans, predicate));
			updateOwnershipDelta(
					ownerBean, 
					required==null?new TreeSet<EncodedTrackBean>():required, 
					current==null?new TreeSet<EncodedTrackBean>():current,
					encodingActions, encodingEventListeners);
		}
	}

	protected void updateOwnershipDelta(
		OwnerBean ownerBean, SortedSet<EncodedTrackBean> required, 
		SortedSet<EncodedTrackBean> current, List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EventException {
		TransactionalEncoderService transactionalEncoderService = getTransactionalEncoderService();
		SortedSet<EncodedTrackBean> missing = new TreeSet<EncodedTrackBean>(CollectionUtils.subtract(required, current));
		for (EncodedTrackBean encodedTrackBean : missing) {
			transactionalEncoderService.own(ownerBean, encodedTrackBean, encodingActions, encodingEventListeners);
		}
		SortedSet<EncodedTrackBean> extra = new TreeSet<EncodedTrackBean>(CollectionUtils.subtract(current, required));
		for (EncodedTrackBean encodedTrackBean : extra) {
			transactionalEncoderService.unown(ownerBean, encodedTrackBean, encodingActions, encodingEventListeners);
		}
	}

	protected void doRemoveMissing(List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) {
		Set<EncodedTrackBean> orphanedEncodedTrackBeans = getEncodedService().findOrphanedEncodedTrackBeans();
		TransactionalEncoderService transactionalEncoderService = getTransactionalEncoderService();
		for (EncodedTrackBean orphanedEncodedTrackBean : orphanedEncodedTrackBeans) {
			try {
				List<EncodingAction> currentEncodingActions = new LinkedList<EncodingAction>();
				transactionalEncoderService.remove(orphanedEncodedTrackBean, currentEncodingActions, encodingEventListeners);
				encodingActions.addAll(currentEncodingActions);
			}
			catch (EncodingException e) {
				log.error("Could not remove track " + orphanedEncodedTrackBean);
			}
		}
	}

	@Override
	public String[] getRequiredPackageNames() {
		return new String[] { "flac", "vorbis-tools", "lame", "id3v2" };
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

	public EncoderConfiguration getEncoderConfiguration() {
		return i_encoderConfiguration;
	}

	public void setEncoderConfiguration(EncoderConfiguration encoderConfiguration) {
		i_encoderConfiguration = encoderConfiguration;
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
}
