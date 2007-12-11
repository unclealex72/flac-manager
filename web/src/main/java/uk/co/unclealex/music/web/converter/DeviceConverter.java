package uk.co.unclealex.music.web.converter;

import uk.co.unclealex.flacconverter.KeyedConverter;
import uk.co.unclealex.music.web.encoded.dao.DeviceDao;
import uk.co.unclealex.music.web.encoded.dao.KeyedDao;
import uk.co.unclealex.music.web.encoded.model.DeviceBean;

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
