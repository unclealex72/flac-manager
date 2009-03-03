package uk.co.unclealex.music.base.dao;

import uk.co.unclealex.hibernate.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.base.model.FlacTrackBean;

public interface FlacTrackDao extends KeyedReadOnlyDao<FlacTrackBean> {

	public FlacTrackBean findByUrl(String url);
	
	public int countTracks();
	
	public FlacTrackBean findTrackStartingWith(String url);
}
