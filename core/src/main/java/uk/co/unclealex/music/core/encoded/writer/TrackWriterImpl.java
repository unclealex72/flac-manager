package uk.co.unclealex.music.core.encoded.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.service.TitleFormatService;
import uk.co.unclealex.flacconverter.encoded.service.TitleFormatServiceFactory;
import uk.co.unclealex.music.core.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.encoded.dao.TrackDataDao;
import uk.co.unclealex.music.core.encoded.model.EncodedTrackBean;
import uk.co.unclealex.music.core.encoded.service.TrackDataStreamIteratorFactory;
import uk.co.unclealex.music.core.encoded.service.TrackStreamService;

@Transactional(readOnly=true)
public class TrackWriterImpl implements TrackWriter {

  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	private TrackDataDao i_trackDataDao;
	private EncodedTrackDao i_encodedTrackDao;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	private TrackDataStreamIteratorFactory i_trackDataStreamIteratorFactory;
	private TrackStreamService i_trackStreamService;

	private Map<TrackStream, TitleFormatService> i_titleFormatServicesByTrackStream;
	private TrackWritingException i_trackWritingException = new TrackWritingException();
	private Map<TrackStream, Set<WritingListener>> i_writingListenersByTrackStream = 
		new HashMap<TrackStream, Set<WritingListener>>();
	
	public void registerWritingListener(WritingListener writingListener, TrackStream trackStream) {
		Map<TrackStream, Set<WritingListener>> writingListenersByTrackStream = getWritingListenersByTrackStream();
		Set<WritingListener> writingListeners = writingListenersByTrackStream.get(trackStream);
		if (writingListeners == null) {
			writingListeners = new HashSet<WritingListener>();
			writingListenersByTrackStream.put(trackStream, writingListeners);
		}
		writingListeners.add(writingListener);
	}
	
	@Override
	public void initialise(Map<TrackStream,TitleFormatService> titleFormatServicesByTrackStream) {
		Map<TrackStream,TitleFormatService> copyOfTitleFormatServicesByTrackStream = 
			new HashMap<TrackStream, TitleFormatService>();
		copyOfTitleFormatServicesByTrackStream.putAll(titleFormatServicesByTrackStream);
		setTitleFormatServicesByTrackStream(copyOfTitleFormatServicesByTrackStream);
	}
	
	public void create() {
		for (TrackStream trackStream : getTrackStreams()) {
			try {
				trackStream.create();
			}
			catch(IOException e) {
				registerIoException(trackStream, e);
			}
		}
	}

	public void write(EncodedTrackBean encodedTrackBean) {
		write(encodedTrackBean, null);
	}

	@Override
	public void writeAll(Collection<EncodedTrackBean> encodedTrackBeans) {
		Collection<TrackStream> trackStreams  = getTitleFormatServicesByTrackStream().keySet();
		registerBeforeFileWrites(trackStreams);
		for (EncodedTrackBean encodedTrackBean : encodedTrackBeans) {
			write(encodedTrackBean, trackStreams);
		}
		registerAfterFilesWritten(trackStreams);
	}
	
	@Override
	public Map<TrackStream, SortedSet<String>> writeAll(
			Map<EncodedTrackBean, Collection<TrackStream>> trackStreamsByTrack) {
		Set<TrackStream> allTrackStreams = new HashSet<TrackStream>();
		for (Collection<TrackStream> trackStreams : trackStreamsByTrack.values()) {
			allTrackStreams.addAll(trackStreams);
		}
		registerBeforeFileWrites(allTrackStreams);
		Map<TrackStream, SortedSet<String>> titlesByStream = new HashMap<TrackStream, SortedSet<String>>();
		for (Map.Entry<EncodedTrackBean, Collection<TrackStream>> entry : trackStreamsByTrack.entrySet()) {
			EncodedTrackBean encodedTrackBean = entry.getKey();
			Collection<TrackStream> trackStreams = entry.getValue();
			Map<TrackStream, String> titleByStream = write(encodedTrackBean, trackStreams);
			for (Map.Entry<TrackStream, String> singleEntry : titleByStream.entrySet()) {
				TrackStream stream = singleEntry.getKey();
				String title = singleEntry.getValue();
				SortedSet<String> titles = titlesByStream.get(stream);
				if (titles == null) {
					titles = new TreeSet<String>();
					titlesByStream.put(stream, titles);
				}
				titles.add(title);
			}
		}
		registerAfterFilesWritten(allTrackStreams);
		return titlesByStream;
	}
	
	@Override
	public Map<TrackStream, String> write(EncodedTrackBean encodedTrackBean, final Collection<TrackStream> trackStreams) {
		Map<TrackStream, String> titlesByTrackStream = new HashMap<TrackStream, String>();
		Predicate<TrackStream> isApplicableTrackStream = new Predicate<TrackStream>() {
			@Override
			public boolean evaluate(TrackStream trackStream) {
				return trackStreams == null || trackStreams.contains(trackStream);
			}
		};
		if (getTrackStreams(isApplicableTrackStream).isEmpty()) {
			return titlesByTrackStream;
		}
		
		Map<TrackStream, OutputStream> outputStreamsByTrackStream = new HashMap<TrackStream, OutputStream>();
		for (TrackStream trackStream : getTrackStreams(isApplicableTrackStream)) {
			TitleFormatService titleFormatService = getTitleFormatServicesByTrackStream().get(trackStream);
			String title = titleFormatService.getTitle(encodedTrackBean);
			titlesByTrackStream.put(trackStream, title);
			try {
				OutputStream outputStream = trackStream.createStream(encodedTrackBean, title);
				if (outputStream != null) {
					outputStreamsByTrackStream.put(trackStream, outputStream);
					for (WritingListener writingListener : getWritingListenersForTrackStream(trackStream)) {
						writingListener.registerFileWrite();
					}
				}
				else {
					for (WritingListener writingListener : getWritingListenersForTrackStream(trackStream)) {
						writingListener.registerFileIgnore(title);
					}					
				}
			}
			catch (IOException e) {
				registerIoException(trackStream, e);
			}
		}
		if (!outputStreamsByTrackStream.isEmpty()) {
			InputStream in = getTrackStreamService().getTrackInputStream(encodedTrackBean);
	    int count = 0;
	    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			try {
				int n;
				while (-1 != (n = in.read(buffer))) {
					for (TrackStream trackStream : getTrackStreams(isApplicableTrackStream)) {
						OutputStream outputStream = outputStreamsByTrackStream.get(trackStream);
						if (outputStream != null) {
							try {
								outputStream.write(buffer, 0, n);
							}
							catch (IOException e) {
								registerIoException(trackStream, e);
							}
						}
					}
	        count += n;
				}
			}
			catch (IOException e) {
				registerIoException(e);
			}
			for (TrackStream trackStream : getTrackStreams(isApplicableTrackStream)) {
				if (outputStreamsByTrackStream.get(trackStream) != null) {
					try {
						trackStream.closeStream();
						for (WritingListener writingListener : getWritingListenersForTrackStream(trackStream)) {
							writingListener.registerFileWritten(titlesByTrackStream.get(trackStream), count);
						}
					}
					catch (IOException e) {
						registerIoException(trackStream, e);
					}
				}
			}
			try {
				in.close();
			}
			catch (IOException e) {
				registerIoException(e);
			}
		}
		return titlesByTrackStream;
	}
	
	@Override
	public void close() throws TrackWritingException {
		for (TrackStream trackStream : getTrackStreams()) {
			try {
				trackStream.close();
			}
			catch (IOException e) {
				registerIoException(trackStream, e);
			}
		}
		TrackWritingException e = getTrackWritingException();
		if (e.requiresThrowing()) {
			throw e;
		}
	}

	public void registerIoException(TrackStream trackStream, IOException ioException) {
		getTrackWritingException().registerException(trackStream, ioException);
		getTitleFormatServicesByTrackStream().remove(trackStream);
		for (WritingListener writingListener : getWritingListenersForTrackStream(trackStream)) {
			writingListener.finish(ioException);
		}
	}
	
	public void registerIoException(IOException ioException) {
		TrackWritingException trackWritingException = getTrackWritingException();
		Map<TrackStream, TitleFormatService> titleFormatServicesByTrackStream = getTitleFormatServicesByTrackStream();
		for (TrackStream trackStream : getTrackStreams()) {
			trackWritingException.registerException(trackStream, ioException);
			for (WritingListener writingListener :  getWritingListenersForTrackStream(trackStream)) {
				writingListener.finish(ioException);
			}
			titleFormatServicesByTrackStream.remove(trackStream);
		}
	}

	protected void registerBeforeFileWrites(Collection<TrackStream> trackStreams) {
		for (TrackStream trackStream : trackStreams) {
			for (WritingListener writingListener : getWritingListenersForTrackStream(trackStream)) {
				try {
					writingListener.beforeFileWrites();
				}
				catch (IOException e) {
					registerIoException(trackStream, e);
				}
			}
		}
	}

	protected void registerAfterFilesWritten(Collection<TrackStream> trackStreams) {
		for (TrackStream trackStream : trackStreams) {
			for (WritingListener writingListener : getWritingListenersForTrackStream(trackStream)) {
				try {
					writingListener.afterFilesWritten();
				}
				catch (IOException e) {
					registerIoException(trackStream, e);
				}
			}
		}
	}

	public Set<TrackStream> getTrackStreams(Predicate<TrackStream> predicate) {
		return new HashSet<TrackStream>(CollectionUtils.select(
				getTitleFormatServicesByTrackStream().keySet(), predicate));
	}
	
	public Set<TrackStream> getTrackStreams() {
		return getTrackStreams(
				new Predicate<TrackStream>() {
					@Override
					public boolean evaluate(TrackStream object) {
						return true;
					}
				});
	}
	
	public void remove(TrackStream trackStream) {
		getTitleFormatServicesByTrackStream().remove(trackStream);
	}
	
	@SuppressWarnings("unchecked")
	public Set<WritingListener> getWritingListenersForTrackStream(TrackStream trackStream) {
		Set<WritingListener> writingListeners = getWritingListenersByTrackStream().get(trackStream);
		return writingListeners == null?Collections.EMPTY_SET:writingListeners;
	}
	
	@Required
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

	public TrackDataStreamIteratorFactory getTrackDataStreamIteratorFactory() {
		return i_trackDataStreamIteratorFactory;
	}

	public void setTrackDataStreamIteratorFactory(
			TrackDataStreamIteratorFactory trackDataStreamIteratorFactory) {
		i_trackDataStreamIteratorFactory = trackDataStreamIteratorFactory;
	}

	public TrackStreamService getTrackStreamService() {
		return i_trackStreamService;
	}

	public void setTrackStreamService(TrackStreamService singleEncoderService) {
		i_trackStreamService = singleEncoderService;
	}

	public Map<TrackStream, TitleFormatService> getTitleFormatServicesByTrackStream() {
		return i_titleFormatServicesByTrackStream;
	}

	public void setTitleFormatServicesByTrackStream(
			Map<TrackStream, TitleFormatService> titleFormatsByTrackStream) {
		this.i_titleFormatServicesByTrackStream = titleFormatsByTrackStream;
	}

	public TrackWritingException getTrackWritingException() {
		return i_trackWritingException;
	}

	public void setTrackWritingException(TrackWritingException trackWritingException) {
		i_trackWritingException = trackWritingException;
	}

	public Map<TrackStream,Set<WritingListener>> getWritingListenersByTrackStream() {
		return i_writingListenersByTrackStream;
	}

	public void setWritingListenersByTrackStream(
			Map<TrackStream, Set<WritingListener>> writingListenersByTrackStream) {
		i_writingListenersByTrackStream = writingListenersByTrackStream;
	}

	public TitleFormatServiceFactory getTitleFormatServiceFactory() {
		return i_titleFormatServiceFactory;
	}

	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}
}
