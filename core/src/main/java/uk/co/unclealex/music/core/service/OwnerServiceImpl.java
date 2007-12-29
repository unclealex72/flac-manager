package uk.co.unclealex.music.core.service;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.dao.OwnerDao;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.model.OwnerBean;

@Service
public class OwnerServiceImpl implements OwnerService {

	private static Log log = LogFactory.getLog(OwnerServiceImpl.class);
	
	private EncodedTrackDao i_encodedTrackDao;
	private OwnerDao i_ownerDao;
	
	@Override
	public SortedSet<EncodedTrackBean> getOwnedEncodedTracks(OwnerBean ownerBean, final EncoderBean encoderBean) {
		SortedSet<EncodedTrackBean> encodedTrackBeans = new TreeSet<EncodedTrackBean>();
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		if (ownerBean.isOwnsAll()) {
			encodedTrackBeans.addAll(encodedTrackDao.findByEncoderBean(encoderBean));
		}
		else {
			for (EncodedArtistBean encodedArtistBean : ownerBean.getEncodedArtistBeans()) {
				encodedTrackBeans.addAll(encodedTrackDao.findByArtistAndEncoderBean(encodedArtistBean, encoderBean));
			}			
			for (EncodedAlbumBean encodedAlbumBean : ownerBean.getEncodedAlbumBeans()) {
				encodedTrackBeans.addAll(encodedTrackDao.findByAlbumAndEncoderBean(encodedAlbumBean, encoderBean));
			}
		}
		return encodedTrackBeans;
	}
	
	@Override
	public void updateOwnership(String ownerName,
			Collection<EncodedArtistBean> encodedArtistBeans,
			Collection<EncodedAlbumBean> encodedAlbumBeans) {
		log.info("Updating ownership.");
		if (encodedArtistBeans == null) {
			encodedArtistBeans = new TreeSet<EncodedArtistBean>();
		}
		if (encodedAlbumBeans == null) {
			encodedAlbumBeans = new TreeSet<EncodedAlbumBean>();
		}
		// Filter out any albums owned by any of the given artists
		for (EncodedArtistBean encodedArtistBean : encodedArtistBeans) {
			encodedAlbumBeans.removeAll(encodedArtistBean.getEncodedAlbumBeans());
		}
		OwnerBean ownerBean = getOwnerDao().findByName(ownerName);
		Collection<EncodedArtistBean> ownedEncodedArtistBeans = ownerBean.getEncodedArtistBeans();
		ownedEncodedArtistBeans.retainAll(encodedArtistBeans);
		ownedEncodedArtistBeans.addAll(encodedArtistBeans);
		Collection<EncodedAlbumBean> ownedEncodedAlbumBeans = ownerBean.getEncodedAlbumBeans();
		ownedEncodedAlbumBeans.retainAll(encodedAlbumBeans);
		ownedEncodedAlbumBeans.addAll(encodedAlbumBeans);
		getOwnerDao().store(ownerBean);
	}
	
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	@Required
	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}
}
