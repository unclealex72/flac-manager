package uk.co.unclealex.flacconverter.encoded.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import uk.co.unclealex.flacconverter.encoded.dao.TrackDataDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.TrackDataBean;

public class TrackDataInputStreamIteratorImpl implements
		TrackDataInputStreamIterator {

	private TrackDataDao i_trackDataDao;
	
	private EncodedTrackBean i_encodedTrackBean;
	private TrackDataBean i_trackDataBean;
	private int i_sequence;
	
	@Override
	public void initialise(EncodedTrackBean encodedTrackBean) {
		setEncodedTrackBean(encodedTrackBean);
		setSequence(0);
	}

	@Override
	public boolean hasNext() {
		return getTrackDataDao().trackWithEncodedTrackBeanAndSequenceExists(
				getEncodedTrackBean(), getSequence());
	}

	@Override
	public InputStream next() {
		TrackDataBean trackDataBean = getTrackDataBean();
		TrackDataDao trackDataDao = getTrackDataDao();
		if (trackDataBean != null) {
			trackDataDao.flush();
			trackDataDao.dismiss(trackDataBean);
		}
		int sequence = getSequence();
		trackDataBean = trackDataDao.findByEncodedTrackBeanAndSequenceExists(getEncodedTrackBean(), sequence);
		setTrackDataBean(trackDataBean);
		setSequence(sequence + 1);
		return new ByteArrayInputStream(trackDataBean.getTrack());
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

	public TrackDataBean getTrackDataBean() {
		return i_trackDataBean;
	}

	public void setTrackDataBean(TrackDataBean trackDataBean) {
		i_trackDataBean = trackDataBean;
	}

}
