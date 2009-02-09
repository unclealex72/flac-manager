package uk.co.unclealex.music.base.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.base.model.EncodedArtistBean;

public interface EncodedArtistDao extends KeyedDao<EncodedArtistBean> {

	public EncodedArtistBean findByIdentifier(String identifier);
	
	public SortedSet<EncodedArtistBean> findAllEmptyArtists();

	public SortedSet<EncodedArtistBean> findByFirstLetter(char firstLetter);

	public EncodedArtistBean findByFilename(String filename);
}
