package uk.co.unclealex.flacconverter.flac.dao;

import uk.co.unclealex.flacconverter.encoded.dao.KeyedDao;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public interface FlacTrackDao extends KeyedDao<FlacTrackBean> {

	public FlacTrackBean findByUrl(String url);
	
	public int countTracks();
}
