package uk.co.unclealex.music.encoder.dao;

import uk.co.unclealex.music.core.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.encoder.model.FlacTrackBean;

public interface FlacTrackDao extends KeyedReadOnlyDao<FlacTrackBean> {

	public FlacTrackBean findByUrl(String url);
	
	public int countTracks();
	
	public FlacTrackBean findTrackStartingWith(String url);
}
