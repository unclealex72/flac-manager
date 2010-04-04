package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.DeviceDao;
import uk.co.unclealex.music.base.model.DeviceBean;

@Transactional
public class HibernateDeviceDao extends
		HibernateKeyedDao<DeviceBean> implements DeviceDao {

	@Override
	public SortedSet<DeviceBean> getAll() {
		return asSortedSet(getSession().createQuery("from deviceBean"));
	}

	@Override
	public DeviceBean createExampleBean() {
		return null;
	}
	
}
