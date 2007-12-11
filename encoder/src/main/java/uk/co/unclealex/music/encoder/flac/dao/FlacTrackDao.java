package uk.co.unclealex.music.encoder.flac.dao;

import uk.co.unclealex.music.core.dao.KeyedDao;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

public interface FlacTrackDao extends KeyedDao<FlacTrackBean> {

	public FlacTrackBean findByUrl(String url);
	
	public int countTracks();
}
