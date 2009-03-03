package uk.co.unclealex.music.base.dao;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.music.base.model.OwnerBean;

public interface OwnerDao extends KeyedDao<OwnerBean> {

	public OwnerBean findByName(String name);

}
