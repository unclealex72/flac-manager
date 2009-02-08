package uk.co.unclealex.music.core.initialise;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedAlbumDao;
import uk.co.unclealex.music.core.dao.EncodedArtistDao;
import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.io.DataInjector;
import uk.co.unclealex.music.core.io.KnownLengthInputStream;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.service.EncodedService;

@Service
@Transactional
public class TrackImporterImpl implements TrackImporter {

	private static final Logger log = Logger.getLogger(TrackImporterImpl.class);
	
	private EncodedService i_encodedService;
	private EncodedArtistDao i_encodedArtistDao;
	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedTrackDao i_encodedTrackDao;
	private DataInjector<EncodedTrackBean> i_encodedTrackDataInjector;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public EncodedTrackBean importTrack(
			InputStream in, int length, EncoderBean encoderBean, 
			String title, String url, int trackNumber, long lastModifiedMillis, EncodedAlbumBean encodedAlbumBean) throws IOException {
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		EncodedTrackBean encodedTrackBean = new EncodedTrackBean();
		encodedTrackBean.setEncoderBean(encoderBean);
		encodedTrackBean.setFlacUrl(url);
		encodedTrackBean.setTimestamp(lastModifiedMillis);
		encodedTrackBean.setTrackNumber(trackNumber);
		encodedTrackBean.setTitle(title);
		getEncodedService().injectFilename(encodedTrackBean);
		encodedTrackBean.setEncodedAlbumBean(encodedAlbumBean);
		getEncodedTrackDataInjector().injectData(encodedTrackBean, new KnownLengthInputStream(in, length));
		encodedTrackDao.store(encodedTrackBean);
		log.info("Stored " + encoderBean.getExtension() + " of " + url);
		encodedTrackDao.clear();
		return encodedTrackBean;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public EncodedArtistDao getEncodedArtistDao() {
		return i_encodedArtistDao;
	}

	@Required
	public void setEncodedArtistDao(EncodedArtistDao encodedArtistDao) {
		i_encodedArtistDao = encodedArtistDao;
	}

	public EncodedAlbumDao getEncodedAlbumDao() {
		return i_encodedAlbumDao;
	}

	@Required
	public void setEncodedAlbumDao(EncodedAlbumDao encodedAlbumDao) {
		i_encodedAlbumDao = encodedAlbumDao;
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	@Required
	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}

	public DataInjector<EncodedTrackBean> getEncodedTrackDataInjector() {
		return i_encodedTrackDataInjector;
	}

	@Required
	public void setEncodedTrackDataInjector(
			DataInjector<EncodedTrackBean> encodedTrackDataInjector) {
		i_encodedTrackDataInjector = encodedTrackDataInjector;
	}

}
