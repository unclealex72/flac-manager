package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.core.model.FlacArtistBean;

public interface FlacArtistDao extends CodeDao<FlacArtistBean> {

	public int countArtistsBeginningWith(char c);
	public SortedSet<FlacArtistBean> getArtistsBeginningWith(char c);
}
