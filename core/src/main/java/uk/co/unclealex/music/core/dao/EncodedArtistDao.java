package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.music.core.model.EncodedArtistBean;

public interface EncodedArtistDao extends KeyedDao<EncodedArtistBean> {

	public EncodedArtistBean findByIdentifier(String identifier);
}
