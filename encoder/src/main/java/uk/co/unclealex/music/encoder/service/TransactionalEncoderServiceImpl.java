package uk.co.unclealex.music.encoder.service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.EncodedAlbumDao;
import uk.co.unclealex.music.base.dao.EncodedArtistDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.service.EncodedService;
import uk.co.unclealex.music.base.service.OwnerService;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.exception.EncodingException;
import uk.co.unclealex.music.encoder.exception.EventException;
import uk.co.unclealex.music.encoder.listener.EncodingEventListener;

@Transactional(rollbackFor=EncodingException.class)
public class TransactionalEncoderServiceImpl implements TransactionalEncoderService {

	private static Logger log = Logger.getLogger(TransactionalEncoderServiceImpl.class);

	private List<EncodingEventListener> i_encodingEventListeners;
	private FlacTrackDao i_flacTrackDao;
	private EncodedTrackDao i_encodedTrackDao;
	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedArtistDao i_encodedArtistDao;
	private EncodedService i_encodedService;
	private OwnerService i_ownerService;
	private OwnerDao i_ownerDao;

	@Override
	public void encode(FlacTrackBean flacTrackBean, EncoderBean encoderBean, 
			List<EncodingAction> encodingActions, Lock lock) throws EncodingException {
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		EncodedTrackBean encodedTrackBean = encodedTrackDao.findByUrlAndEncoderBean(flacTrackBean.getUrl(), encoderBean);
		if (encodedTrackBean == null) {
			log.info("No encoded bean found for track " + flacTrackBean.getUrl() + " and encoder " + encoderBean.getExtension());
			doEncode(flacTrackBean, encoderBean, encodingActions, lock);
		}
		else {
			long flacTimestamp;
			try {
				flacTimestamp = new File(new URI(flacTrackBean.getUrl())).lastModified();
			}
			catch (URISyntaxException e) {
				flacTimestamp = flacTrackBean.getTimestamp().longValue();
			} 
			if (encodedTrackBean.getTimestamp().longValue() < flacTimestamp) {
				log.info("The encoded bean for track " + flacTrackBean.getUrl() + " and encoder " + encoderBean.getExtension() + " is outdated.");
				remove(encodedTrackBean, encodingActions, lock);
				doEncode(flacTrackBean, encoderBean, encodingActions, lock);
			}
		}
		return;
	}
	
	protected boolean doEncode(
			FlacTrackBean flacTrackBean, EncoderBean encoderBean, List<EncodingAction> encodingActions, Lock lock) throws EventException {
		EncodedTrackBean encodedTrackBean;
		lock.lock();
		try {
			FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
			FlacArtistBean flacArtistBean = flacAlbumBean.getFlacArtistBean();
			String artistCode = flacArtistBean.getCode();
			EncodedArtistBean encodedArtistBean = getEncodedArtistDao().findByCode(artistCode);
			EncodedService encodedService = getEncodedService();
			if (encodedArtistBean == null) {
				encodedArtistBean = encodedService.createArtist(flacArtistBean);
				fireEncodedArtistAdded(flacArtistBean, encodedArtistBean, encodingActions, null);
			}
			EncodedAlbumBean encodedAlbumBean = 
				getEncodedAlbumDao().findByArtistCodeAndCode(artistCode, flacAlbumBean.getCode());
			if (encodedAlbumBean == null) {
				encodedAlbumBean = encodedService.createAlbum(encodedArtistBean, flacAlbumBean);
				fireEncodedAlbumAdded(flacAlbumBean, encodedAlbumBean, encodingActions, null);
			}
			encodedTrackBean = encodedService.createTrack(encodedAlbumBean, encoderBean, flacTrackBean);
		}
		finally {
			lock.unlock();
		}
		fireEncodedTrackAdded(flacTrackBean, encodedTrackBean, encodingActions, lock);
		return true;
	}

	@Override
	public void remove(EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions, Lock lock) throws EncodingException {
		lock.lock();
		try {
			String trackCode = encodedTrackBean.getCode();
			EncodedAlbumBean encodedAlbumBean = encodedTrackBean.getEncodedAlbumBean();
			String albumCode = encodedAlbumBean.getCode();
			EncodedArtistBean encodedArtistBean = encodedAlbumBean.getEncodedArtistBean();
			String artistCode = encodedArtistBean.getCode();
			fireEncodedTrackRemoved(artistCode, albumCode, trackCode, encodedTrackBean, encodingActions, lock);
			getEncodedTrackDao().remove(encodedTrackBean);
			if (encodedAlbumBean.getEncodedTrackBeans().isEmpty()) {
				fireEncodedAlbumRemoved(artistCode, albumCode, encodedAlbumBean, encodingActions, null);
				getEncodedAlbumDao().remove(encodedAlbumBean);
				getEncodedArtistDao().store(encodedArtistBean);
				if (encodedArtistBean.getEncodedAlbumBeans().isEmpty()) {
					fireEncodedArtistRemoved(artistCode, encodedArtistBean, encodingActions, null);
					getEncodedArtistDao().remove(encodedArtistBean);
				}
			}
		}
		finally {
			lock.unlock();
		}
	}
	
	@Override
	public void updateOwnership(List<EncodingAction> encodingActions) throws EventException {
		SortedMap<OwnerBean, SortedSet<EncodedTrackBean>> requiredOwnership = new TreeMap<OwnerBean, SortedSet<EncodedTrackBean>>(); 
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		for (Map.Entry<OwnerBean, SortedSet<FlacTrackBean>> entry : getOwnerService().resolveOwnershipByFiles().entrySet()) {
			SortedSet<EncodedTrackBean> encodedTrackBeans = new TreeSet<EncodedTrackBean>();
			for (FlacTrackBean flacTrackBean : entry.getValue()) {
				encodedTrackBeans.addAll(encodedTrackDao.findByUrl(flacTrackBean.getUrl()));
			}
			requiredOwnership.put(entry.getKey(), encodedTrackBeans);
		}
		// Calculate deltas for each owner.
		for (OwnerBean ownerBean : getOwnerDao().getAll()) {
			SortedSet<EncodedTrackBean> required = requiredOwnership.get(ownerBean);
			SortedSet<EncodedTrackBean> current = encodedTrackDao.findByOwnerBean(ownerBean);
			updateOwnershipDelta(
					ownerBean, 
					required==null?new TreeSet<EncodedTrackBean>():required, 
					current==null?new TreeSet<EncodedTrackBean>():current,
					encodingActions);
		}
	}

	protected void updateOwnershipDelta(
		OwnerBean ownerBean, SortedSet<EncodedTrackBean> required, 
		SortedSet<EncodedTrackBean> current, List<EncodingAction> encodingActions) throws EventException {
		SortedSet<EncodedTrackBean> missing = new TreeSet<EncodedTrackBean>(CollectionUtils.subtract(required, current));
		for (EncodedTrackBean encodedTrackBean : missing) {
			fireOwnerAdded(ownerBean, null, encodedTrackBean, encodingActions, null);
		}
		SortedSet<EncodedTrackBean> extra = new TreeSet<EncodedTrackBean>(CollectionUtils.subtract(current, required));
		for (EncodedTrackBean encodedTrackBean : extra) {
			fireOwnerRemoved(ownerBean, encodedTrackBean, encodingActions, null);
		}
	}

	@Override
	public void startEncoding() {
		ExceptionlessListenerCallback callback = new ExceptionlessListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) {
				listener.encodingStarted();
			}
		};
		doFire(callback, null);
	}
	
	@Override
	public void stopEncoding() {
		ExceptionlessListenerCallback callback = new ExceptionlessListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) {
				listener.encodingFinished();
			}
		};
		doFire(callback, null);
	}
	
	protected void fireOwnerRemoved(
			final OwnerBean ownerBean, final EncodedTrackBean encodedTrackBean, 
			final List<EncodingAction> encodingActions, Lock lock) throws EventException{
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.ownerRemoved(ownerBean, encodedTrackBean, encodingActions);
			}
		};
		doFire(callback, lock);
	}

	protected void fireOwnerAdded(
			final OwnerBean ownerBean, final FlacTrackBean flacTrackBean, 
			final EncodedTrackBean encodedTrackBean, final List<EncodingAction> encodingActions,
			Lock lock) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.ownerAdded(ownerBean, encodedTrackBean, encodingActions);
			}
		};
		doFire(callback, lock);
	}

	protected void fireEncodedTrackAdded(
			final FlacTrackBean flacTrackBean, final EncodedTrackBean encodedTrackBean, 
			final List<EncodingAction> encodingActions, Lock lock) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.trackAdded(flacTrackBean, encodedTrackBean, encodingActions);
			}
		};
		doFire(callback, lock);
	}

	protected void fireEncodedAlbumAdded(
			final FlacAlbumBean flacAlbumBean, final EncodedAlbumBean encodedAlbumBean,
			final List<EncodingAction> encodingActions, Lock lock) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.albumAdded(flacAlbumBean, encodedAlbumBean, encodingActions);
			}
		};
		doFire(callback, lock);
	}

	protected void fireEncodedArtistAdded(
			final FlacArtistBean flacArtistBean, final EncodedArtistBean encodedArtistBean, 
			final List<EncodingAction> encodingActions, Lock lock) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.artistAdded(flacArtistBean, encodedArtistBean, encodingActions);
			}
		};
		doFire(callback, lock);
	}

	protected void fireEncodedArtistRemoved(
			final String artistCode, final EncodedArtistBean encodedArtistBean, 
			final List<EncodingAction> encodingActions, Lock lock) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.artistRemoved(encodedArtistBean, encodingActions);
			}
		};
		doFire(callback, lock);
	}

	protected void fireEncodedAlbumRemoved(
			final String artistCode, final String albumCode, 
			final EncodedAlbumBean encodedAlbumBean, final List<EncodingAction> encodingActions,
			Lock lock) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.albumRemoved(encodedAlbumBean, encodingActions);
			}
		};
		doFire(callback, lock);
	}

	protected void fireEncodedTrackRemoved(
			final String artistCode, final String albumCode,
			final String trackCode, final EncodedTrackBean encodedTrackBean, 
			final List<EncodingAction> encodingActions, Lock lock) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.trackRemoved(encodedTrackBean, encodingActions);
			}
		};
		doFire(callback, lock);
	}

	protected void doFire(ListenerCallback callback, Lock lock) throws EventException {
		for (EncodingEventListener listener : getEncodingEventListeners()) {
			boolean synchronisationRequired = lock != null && listener.isSynchronisationRequired();
			if (synchronisationRequired) {
				lock.lock();
			}
			try {
				callback.doInListener(listener);
			}
			finally {
				if (synchronisationRequired) {
					lock.unlock();
				}
			}
		}
	}
	
	protected void doFire(ExceptionlessListenerCallback callback, Lock lock) {
		for (EncodingEventListener listener : getEncodingEventListeners()) {
			boolean synchronisationRequired = lock != null && listener.isSynchronisationRequired();
			if (synchronisationRequired) {
				lock.lock();
			}
			try {
				callback.doInListener(listener);
			}
			finally {
				if (synchronisationRequired) {
					lock.unlock();
				}
			}
		}
	}

	protected interface ListenerCallback {
		public void doInListener(EncodingEventListener listener) throws EventException;
	}
	
	protected interface ExceptionlessListenerCallback {
		public void doInListener(EncodingEventListener listener);
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

	public void setEncodingEventListeners(List<EncodingEventListener> encodingEventListeners) {
		i_encodingEventListeners = encodingEventListeners;
	}

	public EncodedAlbumDao getEncodedAlbumDao() {
		return i_encodedAlbumDao;
	}

	public void setEncodedAlbumDao(EncodedAlbumDao encodedAlbumDao) {
		i_encodedAlbumDao = encodedAlbumDao;
	}

	public EncodedArtistDao getEncodedArtistDao() {
		return i_encodedArtistDao;
	}

	public void setEncodedArtistDao(EncodedArtistDao encodedArtistDao) {
		i_encodedArtistDao = encodedArtistDao;
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
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

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}
}
