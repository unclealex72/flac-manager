package uk.co.unclealex.music.encoder.service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.locks.Lock;

import org.apache.commons.collections15.IteratorUtils;
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
			List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EncodingException {
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		EncodedTrackBean encodedTrackBean = encodedTrackDao.findByUrlAndEncoderBean(flacTrackBean.getUrl(), encoderBean);
		if (encodedTrackBean == null) {
			log.info("No encoded bean found for track " + flacTrackBean.getUrl() + " and encoder " + encoderBean.getExtension());
			doEncode(flacTrackBean, encoderBean, encodingActions, encodingEventListeners);
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
				remove(encodedTrackBean, encodingActions, encodingEventListeners);
				doEncode(flacTrackBean, encoderBean, encodingActions, encodingEventListeners);
			}
		}
		return;
	}
	
	protected boolean doEncode(
			FlacTrackBean flacTrackBean, EncoderBean encoderBean, List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EventException {
		EncodedTrackBean encodedTrackBean;
		FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
		FlacArtistBean flacArtistBean = flacAlbumBean.getFlacArtistBean();
		String artistCode = flacArtistBean.getCode();
		EncodedArtistBean encodedArtistBean = getEncodedArtistDao().findByCode(artistCode);
		EncodedService encodedService = getEncodedService();
		if (encodedArtistBean == null) {
			encodedArtistBean = encodedService.createArtist(flacArtistBean);
			fireEncodedArtistAdded(flacArtistBean, encodedArtistBean, encodingActions, null, encodingEventListeners);
		}
		EncodedAlbumDao encodedAlbumDao = getEncodedAlbumDao();
		EncodedAlbumBean encodedAlbumBean = 
			encodedAlbumDao.findByArtistCodeAndCode(artistCode, flacAlbumBean.getCode());
		if (encodedAlbumBean == null) {
			encodedAlbumBean = encodedService.createAlbum(encodedArtistBean, flacAlbumBean);
			fireEncodedAlbumAdded(flacAlbumBean, encodedAlbumBean, encodingActions, null, encodingEventListeners);
		}
		encodedTrackBean = encodedService.createTrack(encodedAlbumBean, encoderBean, flacTrackBean);
		fireEncodedTrackAdded(flacTrackBean, encodedTrackBean, encodingActions, encodingEventListeners);
		encodedAlbumDao.flush();
		return true;
	}

	@Override
	public void remove(EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EncodingException {
		String trackCode = encodedTrackBean.getCode();
		EncodedAlbumBean encodedAlbumBean = encodedTrackBean.getEncodedAlbumBean();
		String albumCode = encodedAlbumBean.getCode();
		EncodedArtistBean encodedArtistBean = encodedAlbumBean.getEncodedArtistBean();
		String artistCode = encodedArtistBean.getCode();
		fireEncodedTrackRemoved(artistCode, albumCode, trackCode, encodedTrackBean, encodingActions, encodingEventListeners);
		SortedSet<EncodedTrackBean> encodedTrackBeans = encodedAlbumBean.getEncodedTrackBeans();
		encodedTrackBeans.remove(encodedTrackBean);
		getEncodedTrackDao().remove(encodedTrackBean);
		if (encodedTrackBeans.isEmpty()) {
			fireEncodedAlbumRemoved(artistCode, albumCode, encodedAlbumBean, encodingActions, null, encodingEventListeners);
			SortedSet<EncodedAlbumBean> encodedAlbumBeans = encodedArtistBean.getEncodedAlbumBeans();
			encodedAlbumBeans.remove(encodedAlbumBean);
			getEncodedAlbumDao().remove(encodedAlbumBean);
			if (encodedAlbumBeans.isEmpty()) {
				fireEncodedArtistRemoved(artistCode, encodedArtistBean, encodingActions, null, encodingEventListeners);
				getEncodedArtistDao().remove(encodedArtistBean);
			}
		}
	}
	
	@Override
	public void startEncoding(List<EncodingEventListener> encodingEventListeners) {
		ExceptionlessListenerCallback callback = new ExceptionlessListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) {
				listener.encodingStarted();
			}
		};
		doFire(callback, encodingEventListeners);
	}
	
	@Override
	public void stopEncoding(List<EncodingEventListener> encodingEventListeners) {
		ExceptionlessListenerCallback callback = new ExceptionlessListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) {
				listener.encodingFinished();
			}
		};
		doFire(callback, encodingEventListeners);
	}
	
	@Override
	public void own(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions,
			List<EncodingEventListener> encodingEventListeners) throws EventException {
		fireOwnerAdded(ownerBean, encodedTrackBean, encodingActions, encodingEventListeners);
	}
	
	@Override
	public void unown(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions,
			List<EncodingEventListener> encodingEventListeners) throws EventException {
		fireOwnerRemoved(ownerBean, encodedTrackBean, encodingActions, encodingEventListeners);
	}
	
	protected void fireOwnerRemoved(
			final OwnerBean ownerBean, final EncodedTrackBean encodedTrackBean, 
			final List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EventException{
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.ownerRemoved(ownerBean, encodedTrackBean, encodingActions);
			}
		};
		doFire(callback, encodingEventListeners);
	}

	protected void fireOwnerAdded(
			final OwnerBean ownerBean, final EncodedTrackBean encodedTrackBean, 
			final List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.ownerAdded(ownerBean, encodedTrackBean, encodingActions);
			}
		};
		doFire(callback, encodingEventListeners);
	}

	protected void fireEncodedTrackAdded(
			final FlacTrackBean flacTrackBean, final EncodedTrackBean encodedTrackBean, 
			final List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.trackAdded(flacTrackBean, encodedTrackBean, encodingActions);
			}
		};
		doFire(callback, encodingEventListeners);
	}

	protected void fireEncodedAlbumAdded(
			final FlacAlbumBean flacAlbumBean, final EncodedAlbumBean encodedAlbumBean,
			final List<EncodingAction> encodingActions, Lock lock, List<EncodingEventListener> encodingEventListeners) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.albumAdded(flacAlbumBean, encodedAlbumBean, encodingActions);
			}
		};
		doFire(callback, encodingEventListeners);
	}

	protected void fireEncodedArtistAdded(
			final FlacArtistBean flacArtistBean, final EncodedArtistBean encodedArtistBean, 
			final List<EncodingAction> encodingActions, Lock lock, List<EncodingEventListener> encodingEventListeners) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.artistAdded(flacArtistBean, encodedArtistBean, encodingActions);
			}
		};
		doFire(callback, encodingEventListeners);
	}

	protected void fireEncodedArtistRemoved(
			final String artistCode, final EncodedArtistBean encodedArtistBean, 
			final List<EncodingAction> encodingActions, Lock lock, List<EncodingEventListener> encodingEventListeners) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.artistRemoved(encodedArtistBean, encodingActions);
			}
		};
		doFire(callback, encodingEventListeners);
	}

	protected void fireEncodedAlbumRemoved(
			final String artistCode, final String albumCode, 
			final EncodedAlbumBean encodedAlbumBean, final List<EncodingAction> encodingActions,
			Lock lock, List<EncodingEventListener> encodingEventListeners) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.albumRemoved(encodedAlbumBean, encodingActions);
			}
		};
		doFire(callback, encodingEventListeners);
	}

	protected void fireEncodedTrackRemoved(
			final String artistCode, final String albumCode,
			final String trackCode, final EncodedTrackBean encodedTrackBean, 
			final List<EncodingAction> encodingActions, List<EncodingEventListener> encodingEventListeners) throws EventException {
		ListenerCallback callback = new ListenerCallback() {
			@Override
			public void doInListener(EncodingEventListener listener) throws EventException {
				listener.trackRemoved(encodedTrackBean, encodingActions);
			}
		};
		doFire(callback, encodingEventListeners);
	}

	protected Iterable<EncodingEventListener> listEncodingEventListeners(final List<EncodingEventListener> encodingEventListeners) {
		return new Iterable<EncodingEventListener>() {
			@Override
			public Iterator<EncodingEventListener> iterator() {
				return IteratorUtils.chainedIterator(getEncodingEventListeners().iterator(), encodingEventListeners.iterator());
			}
		};
	}
	
	protected void doFire(ListenerCallback callback, List<EncodingEventListener> encodingEventListeners) throws EventException {
		for (EncodingEventListener listener : listEncodingEventListeners(encodingEventListeners)) {
			callback.doInListener(listener);
		}
	}
	
	protected void doFire(ExceptionlessListenerCallback callback, List<EncodingEventListener> encodingEventListeners) {
		for (EncodingEventListener listener : listEncodingEventListeners(encodingEventListeners)) {
			callback.doInListener(listener);
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
