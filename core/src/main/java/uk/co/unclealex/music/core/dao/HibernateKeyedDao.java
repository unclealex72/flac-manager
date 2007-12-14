package uk.co.unclealex.music.core.dao;

import org.hibernate.Query;
import org.hibernate.metadata.ClassMetadata;

import uk.co.unclealex.music.core.model.KeyedBean;

public abstract class HibernateKeyedDao<T extends KeyedBean<T>> extends HibernateKeyedReadOnlyDao<T> {

	public void remove(T keyedBean) {
		ClassMetadata metadata = getSessionFactory().getClassMetadata(keyedBean.getClass());
		String sql = "delete from " + metadata.getEntityName() + " where id = :id";
		Query q = getSession().createQuery(sql);
		q.setInteger("id", keyedBean.getId());
		q.executeUpdate();
	}

	public void store(T keyedBean) {
		getSession().saveOrUpdate(keyedBean);
	}


}
