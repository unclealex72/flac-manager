package uk.co.unclealex.flacconverter.encoded.dao;

import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.model.KeyedBean;

@Transactional
public interface KeyedDao<T extends KeyedBean<T>> {

	public SortedSet<T> getAll();
	public T findById(int id);
	public void dismiss(T keyedBean);
	public void flush();
	public void clear();

}
