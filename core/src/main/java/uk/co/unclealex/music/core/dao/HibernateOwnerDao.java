package uk.co.unclealex.music.core.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.OwnerBean;

@Repository
@Transactional
public class HibernateOwnerDao extends HibernateKeyedDao<OwnerBean>
		implements OwnerDao {

	@Autowired
	public HibernateOwnerDao(@Qualifier("musicSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

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
