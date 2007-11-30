package uk.co.unclealex.flacconverter.encoded.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodingCommandBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

@Transactional
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
	
	public int getMaximumTrackDataLength();
	
	public InputStream getTrackInputStream(EncodedTrackBean encodedTrackBean);
	public OutputStream getTrackOutputStream(EncodedTrackBean encodedTrackBean);
	
}