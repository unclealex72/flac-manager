package uk.co.unclealex.flacconverter.encoded.dao;

import uk.co.unclealex.flacconverter.encoded.model.KeyedBean;

public interface EncodingDao<T extends KeyedBean<T>> extends KeyedDao<T> {

	public void store(T keyedBean);
	public void remove(T keyedBean);

}
