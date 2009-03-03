package uk.co.unclealex.music.base.dao;

import java.util.SortedSet;

import uk.co.unclealex.hibernate.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.base.model.AbstractFlacBean;

public interface CodeDao<T extends AbstractFlacBean<T>> extends KeyedReadOnlyDao<T> {

	public T findByCode(String code);
	
	public SortedSet<T> getAll();
}
