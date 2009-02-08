package uk.co.unclealex.music.core.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.io.DataExtractor;
import uk.co.unclealex.music.core.io.InputStreamCopier;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.core.service.titleformat.TitleFormatServiceFactory;
import uk.co.unclealex.spring.Prototype;

@Prototype
@Transactional
public class TrackWriterImpl implements TrackWriter {

	private EncodedTrackDao i_encodedTrackDao;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	private DataExtractor i_encodedTrackDataExtractor;
	private Map<TrackStream, TitleFormatService> i_titleFormatServicesByTrackStream;
	private InputStreamCopier i_inputStreamCopier;
	
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
			OutputStream out = new NullOutputStream();
			for (TrackStream applicableTrackStream : getTrackStreams(isApplicableTrackStream)) {
				OutputStream nextOut = outputStreamsByTrackStream.get(applicableTrackStream);
				if (nextOut != null) {
					out = new TeeOutputStream(out, nextOut);
				}
			}
			int count = 0;
			try {
				getInputStreamCopier().copy(getEncodedTrackDataExtractor(), encodedTrackBean.getId(), out);
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
	
	public Set<WritingListener> getWritingListenersForTrackStream(TrackStream trackStream) {
		Set<WritingListener> writingListeners = getWritingListenersByTrackStream().get(trackStream);
		return writingListeners == null?new HashSet<WritingListener>():writingListeners;
	}
	
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}
	
	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
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

	@Required
	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}

	public InputStreamCopier getInputStreamCopier() {
		return i_inputStreamCopier;
	}

	@Required
	public void setInputStreamCopier(
			InputStreamCopier inputStreamCopier) {
		i_inputStreamCopier = inputStreamCopier;
	}

	public DataExtractor getEncodedTrackDataExtractor() {
		return i_encodedTrackDataExtractor;
	}

	@Required
	public void setEncodedTrackDataExtractor(DataExtractor encodedTrackDataExtractor) {
		i_encodedTrackDataExtractor = encodedTrackDataExtractor;
	}
}
