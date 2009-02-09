package uk.co.unclealex.music.core.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.model.OwnerBean;

@Repository
@Transactional
public class HibernateOwnerDao extends HibernateKeyedDao<OwnerBean>
		implements OwnerDao {

	@Autowired
	public HibernateOwnerDao(@Qualifier("musicSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public OwnerBean findByName(String name) {
		Query query =
			getSession().createQuery("from OwnerBean where lower(name) = lower(:name)").
			setString("name", name);
		return uniqueResult(query);
	}
	
	@Override
	public OwnerBean createExampleBean() {
		return new OwnerBean();
	}
}
