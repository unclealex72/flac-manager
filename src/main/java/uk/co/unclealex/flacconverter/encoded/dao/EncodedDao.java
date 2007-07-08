package uk.co.unclealex.flacconverter.encoded.dao;

import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.model.KeyedBean;

@Transactional
public interface EncodedDao<T extends KeyedBean<T>> {

	public void store(T keyedBean);
	public void remove(T keyedBean);
	public SortedSet<T> getAll();
	public T findById(int id);
	public void dismiss(T keyedBean);


}
