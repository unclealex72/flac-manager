package uk.co.unclealex.music.web.converter;

import com.opensymphony.xwork2.conversion.annotations.Conversion;

import uk.co.unclealex.hibernate.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.base.dao.DeviceDao;
import uk.co.unclealex.music.base.model.DeviceBean;

@Conversion
public class DeviceConverter extends KeyedConverter<DeviceBean> {

	private DeviceDao i_deviceDao;
	
	@Override
	protected KeyedReadOnlyDao<DeviceBean> getDao() {
		return getDeviceDao();
	}

	public DeviceDao getDeviceDao() {
		return i_deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		i_deviceDao = deviceDao;
	}

}
