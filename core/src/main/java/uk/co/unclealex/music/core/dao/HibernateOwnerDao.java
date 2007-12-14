package uk.co.unclealex.music.core.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.OwnerBean;

@Repository("ownerDao")
@Transactional
public class HibernateOwnerDao extends HibernateKeyedDao<OwnerBean>
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
