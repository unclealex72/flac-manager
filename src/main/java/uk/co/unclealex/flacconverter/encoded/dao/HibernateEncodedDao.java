package uk.co.unclealex.flacconverter.encoded.dao;

import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import uk.co.unclealex.flacconverter.encoded.model.KeyedBean;

public abstract class HibernateEncodedDao<T extends KeyedBean<T>> extends HibernateDaoSupport implements
		EncodedDao<T> {

	@SuppressWarnings("unchecked")
	public T findById(int id) {
		return (T) getSession().get(createExampleBean().getClass(), id);
	}

	@SuppressWarnings("unchecked")
	public SortedSet<T> getAll() {
		SortedSet<T> all = new TreeSet<T>();
		all.addAll(getSession().createCriteria(createExampleBean().getClass()).list());
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
	
	public void remove(T keyedBean) {
		getSession().delete(keyedBean);
	}

	public void store(T keyedBean) {
		getSession().saveOrUpdate(keyedBean);
	}

	@Override
	public void dismiss(T keyedBean) {
		getSession().evict(keyedBean);
	}
	
	public abstract T createExampleBean();
}
