package uk.co.unclealex.music.base.dao;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;

import uk.co.unclealex.hibernate.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.base.model.FlacTrackBean;

public interface FlacTrackDao extends KeyedReadOnlyDao<FlacTrackBean> {

	public FlacTrackBean findByUrl(String url);
	
	public int countTracks();
	
	public SortedSet<FlacTrackBean> findTracksStartingWith(String url);

	public SortedSet<String> getAllUrls();

	public FlacTrackBean findByFile(File flacFile) throws IOException;
}
