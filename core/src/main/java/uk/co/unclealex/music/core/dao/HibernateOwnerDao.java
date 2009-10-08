package uk.co.unclealex.music.core.dao;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.model.OwnerBean;

@Transactional
public class HibernateOwnerDao extends HibernateKeyedDao<OwnerBean> implements OwnerDao {

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
