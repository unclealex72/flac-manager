package uk.co.unclealex.music.core.io;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.KeyedDao;
import uk.co.unclealex.music.base.io.KnownLengthInputStream;
import uk.co.unclealex.music.base.io.KnownLengthInputStreamCallback;
import uk.co.unclealex.music.base.model.EncodedTrackBean;

@Transactional
@Service
public class EncodedTrackDataManager extends AbstractDataManager<EncodedTrackBean> {

	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	public void extractData(int id, KnownLengthInputStreamCallback callback) throws IOException {
		getEncodedTrackDao().streamTrackData(id, callback);
	}
	
	@Override
	protected void doInjectData(EncodedTrackBean encodedTrackBean, KnownLengthInputStream data) throws IOException {
		encodedTrackBean.setTrackData(data);
	}

	@Override
	protected KeyedDao<EncodedTrackBean> getDao() {
		return getEncodedTrackDao();
	}
	
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
}
