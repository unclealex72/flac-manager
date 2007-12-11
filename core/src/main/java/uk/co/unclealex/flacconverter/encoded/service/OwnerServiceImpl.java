package uk.co.unclealex.flacconverter.encoded.service;

import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedAlbumBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodedArtistBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnerBean;

public class OwnerServiceImpl implements OwnerService {

	private EncodedTrackDao i_encodedTrackDao;
	
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
	
	@Required
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
}
