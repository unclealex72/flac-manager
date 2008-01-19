package uk.co.unclealex.music.core.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.HibernateSessionExecutor;
import uk.co.unclealex.music.core.dao.TrackDataDao;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.TrackDataBean;
import uk.co.unclealex.spring.Prototype;

@Transactional(readOnly=true)
@Prototype
public class TrackDataInputStreamIteratorImpl implements
		TrackDataInputStreamIterator {

	private TrackDataDao i_trackDataDao;
	
	private Iterator<Integer> i_trackDataIdIterator;
	private HibernateSessionExecutor i_hibernateSessionExecutor;
	
	@Override
	public void initialise(EncodedTrackBean encodedTrackBean) {
		setTrackDataIdIterator(
				getTrackDataDao().getIdsForEncodedTrackBean(encodedTrackBean).iterator());
	}

	@Override
	public boolean hasNext() {
		return getTrackDataIdIterator().hasNext();
	}

	@Override
	public InputStream next() {
		final int trackDataId = getTrackDataIdIterator().next();
		Callable<byte[]> callable = new Callable<byte[]>() {
			public byte[] call() {
				TrackDataDao trackDataDao = getTrackDataDao();
				TrackDataBean trackDataBean = trackDataDao.findById(trackDataId);
				byte[] track = trackDataBean.getTrack();
				trackDataDao.dismiss(trackDataBean);
				trackDataDao.flush();
				return track;
			}
		};
		FutureTask<byte[]> task = new FutureTask<byte[]>(callable);
		getHibernateSessionExecutor().execute(task);
		byte[] track;
		try {
			track = task.get();
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		return new ByteArrayInputStream(track);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(getClass() + ":remove");
	}

	public TrackDataDao getTrackDataDao() {
		return i_trackDataDao;
	}

	@Required
	public void setTrackDataDao(TrackDataDao trackDataDao) {
		i_trackDataDao = trackDataDao;
	}

	public Iterator<Integer> getTrackDataIdIterator() {
		return i_trackDataIdIterator;
	}

	public void setTrackDataIdIterator(Iterator<Integer> sequenceIterator) {
		i_trackDataIdIterator = sequenceIterator;
	}

	public HibernateSessionExecutor getHibernateSessionExecutor() {
		return i_hibernateSessionExecutor;
	}

	@Required
	public void setHibernateSessionExecutor(
			HibernateSessionExecutor hibernateSessionExecutor) {
		i_hibernateSessionExecutor = hibernateSessionExecutor;
	}


}
