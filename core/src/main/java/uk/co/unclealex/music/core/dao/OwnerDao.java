package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.music.core.model.OwnerBean;

public interface OwnerDao extends KeyedDao<OwnerBean> {

	public OwnerBean findOwnerByNameAndPassword(String username, String encodedPassword);

	public OwnerBean findByName(String name);

}
