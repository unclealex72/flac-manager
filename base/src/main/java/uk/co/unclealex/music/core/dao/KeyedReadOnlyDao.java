package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.KeyedBean;

@Transactional
public interface KeyedReadOnlyDao<T extends KeyedBean<T>> {

	public SortedSet<T> getAll();
	public T findById(int id);
	public void dismiss(T keyedBean);
	public void flush();
	public void clear();

}
