package uk.co.unclealex.flacconverter.flac.dao;

import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;
import uk.co.unclealex.music.core.dao.KeyedDao;

public interface FlacTrackDao extends KeyedDao<FlacTrackBean> {

	public FlacTrackBean findByUrl(String url);
	
	public int countTracks();
}
