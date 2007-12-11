package uk.co.unclealex.music.encoder.flac.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.SortedMap;
import java.util.SortedSet;

import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.writer.TrackWritingException;
import uk.co.unclealex.music.encoder.flac.model.DownloadCartBean;
import uk.co.unclealex.music.encoder.flac.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.flac.model.FlacArtistBean;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

public interface DownloadCartService {

	public SortedMap<FlacArtistBean, SortedMap<FlacAlbumBean, SortedSet<FlacTrackBean>>>
		createFullView(DownloadCartBean downloadCartBean);
	
	public SortedSet<EncodedTrackBean> getEncodedTrackBeans(DownloadCartBean downloadCartBean, EncoderBean encoderBean);
	
	public void writeAsZip(
			DownloadCartBean downloadCartBean, String titleFormat, EncoderBean encoderBean, OutputStream out) throws IOException, TrackWritingException;
}
