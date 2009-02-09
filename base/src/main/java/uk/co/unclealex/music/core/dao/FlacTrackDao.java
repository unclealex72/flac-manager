package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.music.core.model.FlacTrackBean;

public interface FlacTrackDao extends KeyedReadOnlyDao<FlacTrackBean> {

	public FlacTrackBean findByUrl(String url);
	
	public int countTracks();
	
	public FlacTrackBean findTrackStartingWith(String url);
}
