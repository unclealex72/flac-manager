package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.music.core.encoded.model.OwnerBean;

public class HibernateOwnerDao extends HibernateEncodingDao<OwnerBean>
		implements OwnerDao {

	@Override
	public OwnerBean findOwnerByNameAndPassword(String username, String encodedPassword) {
		OwnerBean exampleBean = createExampleBean();
		exampleBean.setName(username);
		exampleBean.setPasswordHash(encodedPassword);
		return findByExample(exampleBean);
	}
	
	@Override
	public OwnerBean findByName(String name) {
		OwnerBean exampleBean = createExampleBean();
		exampleBean.setName(name);
		return findByExample(exampleBean);
	}
	@Override
	public OwnerBean createExampleBean() {
		return new OwnerBean();
	}
}
