package uk.co.unclealex.flacconverter.encoded.dao;

import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;

public class HibernateDeviceDao extends HibernateEncodedDao<DeviceBean>
		implements DeviceDao {

	@Override
	public DeviceBean createExampleBean() {
		return new DeviceBean();
	}
}
