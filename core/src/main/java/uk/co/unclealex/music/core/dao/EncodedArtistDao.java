package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.core.model.EncodedArtistBean;

public interface EncodedArtistDao extends KeyedDao<EncodedArtistBean> {

	public EncodedArtistBean findByIdentifier(String identifier);
	
	public SortedSet<EncodedArtistBean> findAllEmptyArtists();

	public SortedSet<EncodedArtistBean> findByFirstLetter(char firstLetter);

	public EncodedArtistBean findByName(String name);
}
