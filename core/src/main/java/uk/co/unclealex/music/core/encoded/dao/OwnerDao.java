package uk.co.unclealex.music.core.encoded.dao;

import uk.co.unclealex.music.core.encoded.model.OwnerBean;

public interface OwnerDao extends EncodingDao<OwnerBean> {

	public OwnerBean findOwnerByNameAndPassword(String username, String encodedPassword);

	public OwnerBean findByName(String name);

}
