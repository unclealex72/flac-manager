package uk.co.unclealex.music.core.encoded.dao;

import uk.co.unclealex.music.core.encoded.model.DeviceBean;

public class HibernateDeviceDao extends HibernateEncodingDao<DeviceBean>
		implements DeviceDao {

	@Override
	public DeviceBean createExampleBean() {
		return new DeviceBean();
	}
}
