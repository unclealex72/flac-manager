package uk.co.unclealex.music.encoder.flac.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.co.unclealex.music.core.dao.HibernateKeyedReadOnlyDao;
import uk.co.unclealex.music.encoder.flac.model.AbstractFlacBean;

public abstract class HibernateCodeDao<T extends AbstractFlacBean<T>> extends HibernateKeyedReadOnlyDao<T> implements CodeDao<T> {

	@Autowired
	public HibernateCodeDao(@Qualifier("flacSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public T findByCode(String code) {
		T example = createExampleBean();
		example.setCode(code);
		return findByExample(example);
	}
}
