package uk.co.unclealex.music.core.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.DeviceDao;
import uk.co.unclealex.music.base.model.DeviceBean;
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
	public DeviceBean findByIdentifier(String identifier) {
		DeviceBean deviceBean = new DeviceBean();
		deviceBean.setIdentifier(identifier);
		return findByExample(deviceBean);
	}
	
	@Override
	public DeviceBean createExampleBean() {
		return new DeviceBean();
	}
}
