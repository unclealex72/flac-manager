package uk.co.unclealex.music.core.encoded.dao;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import uk.co.unclealex.music.core.encoded.model.KeyedBean;

public abstract class HibernateKeyedDao<T extends KeyedBean<T>> extends HibernateDaoSupport implements
		KeyedDao<T> {

	protected Logger log = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	public T findById(int id) {
		return (T) getSession().get(createExampleBean().getClass(), id);
	}

	@SuppressWarnings("unchecked")
	public SortedSet<T> getAll() {
		SortedSet<T> all = new TreeSet<T>();
		T exampleBean = createExampleBean();
		all.addAll(getSession().createCriteria(exampleBean.getClass()).add(Example.create(exampleBean)).list());
		return all;
	}

	@SuppressWarnings("unchecked")
	protected SortedSet<T> getAllByExample(T exampleBean) {
		SortedSet<T> all = new TreeSet<T>();
		all.addAll(createCriteria(exampleBean).list());
		return all;		
	}
	
	@SuppressWarnings("unchecked")
	protected T findByExample(T exampleBean) {
		return (T) createCriteria(exampleBean).uniqueResult();
	}
	
	protected Criteria createCriteria(T exampleBean) {
		return getSession().createCriteria(exampleBean.getClass()).add(Example.create(exampleBean));
	}
	
	@Override
	public void flush() {
		getSession().flush();
	}
	
	@Override
	public void dismiss(T keyedBean) {
		getSession().evict(keyedBean);
		getSessionFactory().evict(createExampleBean().getClass(), keyedBean.getId());
	}
	
	@Override
	public void clear() {
		flush();
		getSession().clear();
	}
	
	public abstract T createExampleBean();
}
