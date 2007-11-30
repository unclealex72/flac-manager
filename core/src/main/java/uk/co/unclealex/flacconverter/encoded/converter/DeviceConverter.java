package uk.co.unclealex.flacconverter.encoded.converter;

import uk.co.unclealex.flacconverter.converter.KeyedConverter;
import uk.co.unclealex.flacconverter.encoded.dao.DeviceDao;
import uk.co.unclealex.flacconverter.encoded.dao.KeyedDao;
import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;

public class DeviceConverter extends KeyedConverter<DeviceBean> {

	private DeviceDao i_deviceDao;
	
	@Override
	protected KeyedDao<DeviceBean> getDao() {
		return getDeviceDao();
	}

	public DeviceDao getDeviceDao() {
		return i_deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		i_deviceDao = deviceDao;
	}

}
