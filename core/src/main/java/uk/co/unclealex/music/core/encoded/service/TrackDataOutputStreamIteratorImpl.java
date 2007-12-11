package uk.co.unclealex.music.core.encoded.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TreeSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.dao.TrackDataDao;
import uk.co.unclealex.music.core.encoded.model.EncodedTrackBean;
import uk.co.unclealex.music.core.encoded.model.TrackDataBean;

@Transactional
public class TrackDataOutputStreamIteratorImpl implements
		TrackDataOutputStreamIterator {

	private EncodedTrackBean i_encodedTrackBean;
	private int i_sequence;
	
	private TrackDataDao i_trackDataDao;
	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	public void initialise(EncodedTrackBean encodedTrackBean) {
		setEncodedTrackBean(encodedTrackBean);
		encodedTrackBean.setTrackDataBeans(new TreeSet<TrackDataBean>());
		getTrackDataDao().flush();
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public OutputStream next() {
		EncodedTrackBean encodedTrackBean = getEncodedTrackBean();
		final TrackDataBean trackDataBean = new TrackDataBean();
		trackDataBean.setEncodedTrackBean(encodedTrackBean);
		trackDataBean.setSequence(getSequence());
		setSequence(getSequence() + 1);
		ByteArrayOutputStream out = new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				TrackDataDao trackDataDao = getTrackDataDao();
				trackDataBean.setTrack(toByteArray());
				trackDataDao.store(trackDataBean);
				trackDataDao.flush();
				trackDataDao.dismiss(trackDataBean);
				super.close();
			}
		};
		return out;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(getClass() + ":remove");
	}

	public EncodedTrackBean getEncodedTrackBean() {
		return i_encodedTrackBean;
	}

	public void setEncodedTrackBean(EncodedTrackBean encodedTrackBean) {
		i_encodedTrackBean = encodedTrackBean;
	}

	public int getSequence() {
		return i_sequence;
	}

	public void setSequence(int sequence) {
		i_sequence = sequence;
	}

	public TrackDataDao getTrackDataDao() {
		return i_trackDataDao;
	}

	public void setTrackDataDao(TrackDataDao trackDataDao) {
		i_trackDataDao = trackDataDao;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
}
