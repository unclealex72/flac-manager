package uk.co.unclealex.music.encoder.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.BlockingQueue;

import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;

public interface SingleEncoderService extends Serializable {

	public int encode(EncoderBean encoderBean, FlacTrackBean flacTrackBean,
			EncodingClosure closure, Map<String, File> commandCache) throws IOException;

	/**
	 * Encode a file
	 * @param encodingCommandBean
	 * @param commandCache
	 * @return The length of the file encoded, or null if encoding was not needed.
	 * @throws IOException
	 */
	public EncodedTrackBean encode(EncodingCommandBean encodingCommandBean, Map<String, File> commandCache) throws IOException;

	public File createCommandFile(EncoderBean encoderBean) throws IOException;

	public int removeDeleted(Collection<EncodingEventListener> encodingEventListeners);

	public void updateMissingAlbumInformation();

	public void updateOwnership();

	public void updateAllFilenames();

	public void populateCommandCache(SortedMap<String, File> commandCache) throws IOException;

	public void offerAll(BlockingQueue<EncodingCommandBean> encodingCommandBeans);

}