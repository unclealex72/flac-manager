package uk.co.unclealex.flacconverter.encoded.dao;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;

public class HibernateDeviceDao extends HibernateEncodingDao<DeviceBean>
		implements DeviceDao {

	@Override
	public DeviceBean createExampleBean() {
		return new DeviceBean();
	}
}
