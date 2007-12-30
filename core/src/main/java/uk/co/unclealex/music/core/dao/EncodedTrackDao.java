package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;

public interface EncodedTrackDao extends KeyedDao<EncodedTrackBean> {

	public EncodedTrackBean findByUrlAndEncoderBean(String url, EncoderBean encoderBean);

	public SortedSet<? extends EncodedTrackBean> findByEncoderBean(EncoderBean encoderBean);

	public SortedSet<? extends EncodedTrackBean> findByArtistAndEncoderBean(
			EncodedArtistBean encodedArtistBean, EncoderBean encoderBean);

	public SortedSet<? extends EncodedTrackBean> findByAlbumAndEncoderBean(
			EncodedAlbumBean encodedAlbumBean, EncoderBean encoderBean);
	
	public SortedSet<? extends EncodedTrackBean> findTracksWithoutAnAlbum();

	public EncodedTrackBean findByAlbumAndEncoderBeanAndTrackNumber(
			EncodedAlbumBean encodedAlbumBean, EncoderBean encoderBean,
			int trackNumber);

	public SortedSet<? extends EncodedTrackBean> findByArtist(EncodedArtistBean encodedArtistBean);
}
