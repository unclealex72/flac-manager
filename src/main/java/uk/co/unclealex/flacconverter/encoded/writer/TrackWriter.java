package uk.co.unclealex.flacconverter.encoded.writer;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.service.titleformat.TitleFormatService;

public interface TrackWriter {

	public void initialise(Map<TrackStream,TitleFormatService> titleFormatServicesByTrackStream);
	
	public void create();
	
	/**
	 * 
	 * @param trackStreamsByTrack
	 * @return A sorted set of all the titles written for each track stream.
	 */
	public Map<TrackStream, SortedSet<String>> writeAll(Map<EncodedTrackBean, Collection<TrackStream>> trackStreamsByTrack);
	
	public void writeAll(Collection<EncodedTrackBean> encodedTrackBeans);

	public void write(EncodedTrackBean encodedTrackBean);

	/**
	 * 
	 * @param encodedTrackBean
	 * @param trackStreams
	 * @return The title written for each stream.
	 */
	public Map<TrackStream, String> write(EncodedTrackBean encodedTrackBean, Collection<TrackStream> trackStreams);
	
	public void close() throws TrackWritingException;

	public void registerWritingListener(WritingListener writingListener, TrackStream trackStream);

}
