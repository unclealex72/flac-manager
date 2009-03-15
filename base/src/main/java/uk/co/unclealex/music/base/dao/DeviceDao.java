package uk.co.unclealex.music.base.dao;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.music.base.model.DeviceBean;

public interface DeviceDao extends KeyedDao<DeviceBean> {

	public DeviceBean findByIdentifier(String identifier);

}
