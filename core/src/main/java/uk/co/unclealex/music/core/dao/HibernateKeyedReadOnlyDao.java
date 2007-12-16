package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import uk.co.unclealex.music.core.model.KeyedBean;

public abstract class HibernateKeyedReadOnlyDao<T extends KeyedBean<T>> extends HibernateDaoSupport implements
		KeyedReadOnlyDao<T> {

	protected Logger log = Logger.getLogger(getClass());
	
	public HibernateKeyedReadOnlyDao(SessionFactory sessionFactory) {
		super();
		setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public T findById(int id) {
		return (T) getSession().get(createExampleBean().getClass(), id);
	}

	public SortedSet<T> getAll() {
		T exampleBean = createExampleBean();
		return asSortedSet(getSession().createCriteria(exampleBean.getClass()).add(Example.create(exampleBean)));
	}

	protected SortedSet<T> getAllByExample(T exampleBean) {
		return asSortedSet(createCriteria(exampleBean));
	}
	
	@SuppressWarnings("unchecked")
	protected SortedSet<T> asSortedSet(Query q) {
		return new TreeSet<T>(q.list());
	}

	@SuppressWarnings("unchecked")
	protected SortedSet<T> asSortedSet(Criteria c) {
		return new TreeSet<T>(c.list());
	}

	@SuppressWarnings("unchecked")
	protected <O> SortedSet<O> asSortedSet(Query q, Class<O> clazz) {
		return new TreeSet<O>(q.list());
	}

	@SuppressWarnings("unchecked")
	protected <O> SortedSet<O> asSortedSet(Criteria c, Class<O> clazz) {
		return new TreeSet<O>(c.list());
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
