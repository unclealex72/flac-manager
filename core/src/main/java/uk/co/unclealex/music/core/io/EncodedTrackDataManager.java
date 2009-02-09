package uk.co.unclealex.music.core.io;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.io.DataManager;
import uk.co.unclealex.music.base.io.KnownLengthInputStream;
import uk.co.unclealex.music.base.io.KnownLengthInputStreamCallback;
import uk.co.unclealex.music.base.model.EncodedTrackBean;

@Transactional
@Service
public class EncodedTrackDataManager implements DataManager<EncodedTrackBean> {

	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	public void extractData(int id, KnownLengthInputStreamCallback callback) throws IOException {
		getEncodedTrackDao().streamTrackData(id, callback);
	}
	
	@Override
	public void injectData(EncodedTrackBean encodedTrackBean, KnownLengthInputStream data) throws IOException {
		encodedTrackBean.setTrackData(data);
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
}
