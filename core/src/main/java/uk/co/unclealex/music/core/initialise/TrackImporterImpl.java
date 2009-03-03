package uk.co.unclealex.music.core.initialise;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.EncodedAlbumDao;
import uk.co.unclealex.music.base.dao.EncodedArtistDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.initialise.TrackImporter;
import uk.co.unclealex.music.base.io.DataInjector;
import uk.co.unclealex.music.base.io.KnownLengthInputStream;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.service.EncodedService;

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
	@Transactional(rollbackFor=IOException.class)
	public EncodedTrackBean importTrack(
			InputStream in, int length, EncoderBean encoderBean, 
			String title, String url, int trackNumber, long lastModifiedMillis, EncodedAlbumBean encodedAlbumBean) throws IOException {
		EncodedTrackBean encodedTrackBean = new EncodedTrackBean();
		encodedTrackBean.setEncoderBean(encoderBean);
		encodedTrackBean.setEncodedAlbumBean(encodedAlbumBean);
		encodedTrackBean.setFlacUrl(url);
		encodedTrackBean.setTimestamp(lastModifiedMillis);
		encodedTrackBean.setTrackNumber(trackNumber);
		encodedTrackBean.setTitle(title);
		getEncodedService().injectFilename(encodedTrackBean);
		getEncodedTrackDataInjector().injectData(encodedTrackBean, new KnownLengthInputStream(in, length));
		log.info("Stored " + encoderBean.getExtension() + " of " + url);
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
