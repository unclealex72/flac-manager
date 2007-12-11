package uk.co.unclealex.music.encoder.flac.dao;

import uk.co.unclealex.music.core.dao.HibernateKeyedDao;
import uk.co.unclealex.music.encoder.flac.model.AbstractFlacBean;

public abstract class HibernateCodeDao<T extends AbstractFlacBean<T>> extends HibernateKeyedDao<T> implements CodeDao<T> {

	public T findByCode(String code) {
		T example = createExampleBean();
		example.setCode(code);
		return findByExample(example);
	}
}
