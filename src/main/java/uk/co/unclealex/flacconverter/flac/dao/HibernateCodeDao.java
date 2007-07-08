package uk.co.unclealex.flacconverter.flac.dao;

import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import uk.co.unclealex.flacconverter.flac.model.CodedBean;

public abstract class HibernateCodeDao<T extends CodedBean<T>> extends HibernateDaoSupport implements CodeDao<T> {

	public T findByCode(String code) {
		T example = createBlankExample();
		example.setCode(code);
		return findByExample(example);
	}
	
	@SuppressWarnings("unchecked")
	public SortedSet<T> getAll() {
		SortedSet<T> all = new TreeSet<T>();
		all.addAll(createExampleCriteria(createBlankExample()).list());
		return all;
	}
	protected Criteria createExampleCriteria(T example) {
		return getSession().createCriteria(example.getClass()).add(Example.create(example));
	}
	
	@SuppressWarnings("unchecked")
	protected T findByExample(T example) {
		return (T) createExampleCriteria(example).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	protected SortedSet<T> findAllByExample(T example) {
		return new TreeSet<T>(createExampleCriteria(example).list());
	}

	protected abstract T createBlankExample();
}
