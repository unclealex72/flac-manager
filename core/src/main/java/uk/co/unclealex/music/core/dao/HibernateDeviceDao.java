package uk.co.unclealex.music.core.dao;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.DeviceBean;

@Transactional
public class HibernateDeviceDao extends HibernateKeyedDao<DeviceBean>
		implements DeviceDao {

	@Override
	public DeviceBean createExampleBean() {
		return new DeviceBean();
	}
}
