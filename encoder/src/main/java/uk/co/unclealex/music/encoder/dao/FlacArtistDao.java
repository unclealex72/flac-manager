package uk.co.unclealex.music.encoder.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.encoder.model.FlacArtistBean;

public interface FlacArtistDao extends CodeDao<FlacArtistBean> {

	public int countArtistsBeginningWith(char c);
	public SortedSet<FlacArtistBean> getArtistsBeginningWith(char c);
}
