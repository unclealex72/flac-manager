package uk.co.unclealex.music.web.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.SortedMap;
import java.util.SortedSet;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.writer.TrackWritingException;
import uk.co.unclealex.music.web.model.DownloadCartBean;

public interface DownloadCartService {

	public SortedMap<EncodedArtistBean, SortedMap<EncodedAlbumBean, SortedSet<EncodedTrackBean>>>
		createFullView(DownloadCartBean downloadCartBean);
	
	public SortedSet<EncodedTrackBean> getEncodedTrackBeans(DownloadCartBean downloadCartBean, EncoderBean encoderBean);
	
	public void writeAsZip(
			DownloadCartBean downloadCartBean, String titleFormat, EncoderBean encoderBean, OutputStream out) throws IOException, TrackWritingException;
}
