package uk.co.unclealex.flacconverter.flac.dao;

import java.util.SortedSet;

import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public interface FlacTrackDao {

	public SortedSet<FlacTrackBean> getAllTracks();
	
	public FlacTrackBean findByUrl(String url);
}
