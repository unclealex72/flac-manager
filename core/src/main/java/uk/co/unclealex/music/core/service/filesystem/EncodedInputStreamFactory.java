package uk.co.unclealex.music.core.service.filesystem;

import java.io.InputStream;

import org.apache.commons.collections15.Transformer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.service.TrackStreamService;

@Service
@Transactional
public class EncodedInputStreamFactory implements Transformer<Integer, InputStream> {

	private EncodedTrackDao i_encodedTrackDao;
	private TrackStreamService i_trackStreamService;
	
	@Override
	public InputStream transform(Integer id) {
		EncodedTrackBean encodedTrackBean = getEncodedTrackDao().findById(id);
		return getTrackStreamService().getTrackInputStream(encodedTrackBean);
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public TrackStreamService getTrackStreamService() {
		return i_trackStreamService;
	}

	@Required
	public void setTrackStreamService(TrackStreamService trackStreamService) {
		i_trackStreamService = trackStreamService;
	}
}
