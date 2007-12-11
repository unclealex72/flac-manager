package uk.co.unclealex.music.core.encoded.service;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import uk.co.unclealex.music.core.encoded.model.EncodedTrackBean;

public class SpringTrackDataStreamIteratorFactory implements
		TrackDataStreamIteratorFactory, BeanFactoryAware {

	private BeanFactory i_beanFactory;
	
	@Override
	public TrackDataInputStreamIterator createTrackDataInputStreamIterator(
			EncodedTrackBean encodedTrackBean) {
		TrackDataInputStreamIterator iterator =
			(TrackDataInputStreamIterator)
				getBeanFactory().getBean("trackDataInputStreamIterator", TrackDataInputStreamIterator.class);
		iterator.initialise(encodedTrackBean);
		return iterator;
	}

	@Override
	public TrackDataOutputStreamIterator createTrackDataOutputStreamIterator(
			EncodedTrackBean encodedTrackBean) {
		TrackDataOutputStreamIterator iterator =
			(TrackDataOutputStreamIterator)
				getBeanFactory().getBean("trackDataOutputStreamIterator", TrackDataOutputStreamIterator.class);
		iterator.initialise(encodedTrackBean);
		return iterator;
	}

	public BeanFactory getBeanFactory() {
		return i_beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		i_beanFactory = beanFactory;
	}
}
