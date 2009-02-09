package uk.co.unclealex.music.encoder.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;

public interface SingleEncoderService extends Serializable {

	public int encode(EncoderBean encoderBean, FlacTrackBean flacTrackBean,
			EncodingClosure closure, Map<EncoderBean, File> commandCache) throws IOException;

	/**
	 * Encode a file
	 * @param encodingCommandBean
	 * @param commandCache
	 * @return The length of the file encoded, or null if encoding was not needed.
	 * @throws IOException
	 */
	public EncodedTrackBean encode(EncodingCommandBean encodingCommandBean, Map<EncoderBean, File> commandCache) throws IOException;

	@Transactional(rollbackFor = IOException.class)
	public File createCommandFile(EncoderBean encoderBean) throws IOException;
	
}