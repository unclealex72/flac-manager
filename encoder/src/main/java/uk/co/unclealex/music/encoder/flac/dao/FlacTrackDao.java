package uk.co.unclealex.music.encoder.flac.dao;

import uk.co.unclealex.music.core.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

public interface FlacTrackDao extends KeyedReadOnlyDao<FlacTrackBean> {

	public FlacTrackBean findByUrl(String url);
	
	public int countTracks();
}
