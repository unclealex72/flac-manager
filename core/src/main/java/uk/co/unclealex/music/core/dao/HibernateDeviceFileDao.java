package uk.co.unclealex.music.core.dao;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.DeviceFileDao;
import uk.co.unclealex.music.base.model.DeviceFileBean;

@Transactional
public class HibernateDeviceFileDao extends
		HibernateKeyedDao<DeviceFileBean> implements DeviceFileDao {

	@Override
	public DeviceFileBean createExampleBean() {
		return new DeviceFileBean();
	}

}
