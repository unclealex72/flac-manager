package uk.co.unclealex.flacconverter.encoded.dao;

import uk.co.unclealex.flacconverter.encoded.model.KeyedBean;

public abstract class HibernateEncodingDao<T extends KeyedBean<T>> extends HibernateKeyedDao<T> {

	public void remove(T keyedBean) {
		getSession().delete(keyedBean);
	}

	public void store(T keyedBean) {
		getSession().saveOrUpdate(keyedBean);
	}


}
