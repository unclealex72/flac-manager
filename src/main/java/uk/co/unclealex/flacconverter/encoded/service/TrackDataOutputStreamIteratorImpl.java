package uk.co.unclealex.flacconverter.encoded.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.TreeSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.dao.TrackDataDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.TrackDataBean;

@Transactional
public class TrackDataOutputStreamIteratorImpl implements
		TrackDataOutputStreamIterator {

	private EncodedTrackBean i_encodedTrackBean;
	private int i_sequence;
	private TrackDataBean i_currentTrackDataBean;
	private ByteArrayOutputStream i_currentByteArrayOutputStream;
	
	private TrackDataDao i_trackDataDao;
	
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
		TrackDataBean trackDataBean = getCurrentTrackDataBean();
		TrackDataDao trackDataDao = getTrackDataDao();
		if (trackDataBean != null) {
			trackDataBean.setTrack(getCurrentByteArrayOutputStream().toByteArray());
			trackDataDao.store(trackDataBean);
			trackDataDao.flush();
			trackDataDao.dismiss(trackDataBean);
		}
		trackDataBean = new TrackDataBean();
		trackDataBean.setEncodedTrackBean(encodedTrackBean);
		trackDataBean.setSequence(getSequence());
		setSequence(getSequence() + 1);
		setCurrentByteArrayOutputStream(new ByteArrayOutputStream());
		return getCurrentByteArrayOutputStream();
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

	public TrackDataBean getCurrentTrackDataBean() {
		return i_currentTrackDataBean;
	}

	public void setCurrentTrackDataBean(TrackDataBean currentTrackDataBean) {
		i_currentTrackDataBean = currentTrackDataBean;
	}

	public TrackDataDao getTrackDataDao() {
		return i_trackDataDao;
	}

	public void setTrackDataDao(TrackDataDao trackDataDao) {
		i_trackDataDao = trackDataDao;
	}

	public ByteArrayOutputStream getCurrentByteArrayOutputStream() {
		return i_currentByteArrayOutputStream;
	}

	public void setCurrentByteArrayOutputStream(
			ByteArrayOutputStream currentByteArrayOutputStream) {
		i_currentByteArrayOutputStream = currentByteArrayOutputStream;
	}
}
