package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.hibernate.dao.HibernateKeyedReadOnlyDao;
import uk.co.unclealex.music.base.dao.CodeDao;
import uk.co.unclealex.music.base.model.AbstractFlacBean;

public abstract class HibernateCodeDao<T extends AbstractFlacBean<T>> extends HibernateKeyedReadOnlyDao<T> implements CodeDao<T> {

	public T findByCode(String code) {
		T example = createExampleBean();
		example.setCode(code);
		return findByExample(example);
	}
}
