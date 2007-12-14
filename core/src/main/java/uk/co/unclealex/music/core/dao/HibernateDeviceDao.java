package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.music.core.model.DeviceBean;

public class HibernateDeviceDao extends HibernateKeyedDao<DeviceBean>
		implements DeviceDao {

	@Override
	public DeviceBean createExampleBean() {
		return new DeviceBean();
	}
}
