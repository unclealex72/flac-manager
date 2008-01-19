package uk.co.unclealex.music.core.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.DeviceBean;
import uk.co.unclealex.spring.Prototype;

@Transactional
@Prototype
public class HibernateDeviceDao extends HibernateKeyedDao<DeviceBean>
		implements DeviceDao {

	@Autowired
	public HibernateDeviceDao(@Qualifier("musicSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public DeviceBean createExampleBean() {
		return new DeviceBean();
	}
}
