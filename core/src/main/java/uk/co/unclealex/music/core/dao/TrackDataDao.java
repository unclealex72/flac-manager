package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.TrackDataBean;

public interface TrackDataDao extends EncodingDao<TrackDataBean> {

	public TrackDataBean findByEncodedTrackBeanAndSequence(
			EncodedTrackBean encodedTrackBean, int sequence);

	public SortedSet<Integer> getIdsForEncodedTrackBean(
			EncodedTrackBean encodedTrackBean);

	public void removeById(int id);
}
