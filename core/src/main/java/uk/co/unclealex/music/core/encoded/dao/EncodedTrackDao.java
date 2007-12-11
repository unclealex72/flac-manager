package uk.co.unclealex.music.core.encoded.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.core.encoded.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.encoded.model.EncodedArtistBean;
import uk.co.unclealex.music.core.encoded.model.EncodedTrackBean;
import uk.co.unclealex.music.core.encoded.model.EncoderBean;

public interface EncodedTrackDao extends EncodingDao<EncodedTrackBean> {

	public EncodedTrackBean findByUrlAndEncoderBean(String url, EncoderBean encoderBean);

	public SortedSet<? extends EncodedTrackBean> findByEncoderBean(EncoderBean encoderBean);

	public SortedSet<? extends EncodedTrackBean> findByArtistAndEncoderBean(
			EncodedArtistBean encodedArtistBean, EncoderBean encoderBean);

	public SortedSet<? extends EncodedTrackBean> findByAlbumAndEncoderBean(
			EncodedAlbumBean encodedAlbumBean, EncoderBean encoderBean);
}
