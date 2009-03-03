package uk.co.unclealex.music.core.io;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.model.EncodedTrackBean;

@Service
@Transactional(rollbackFor=IOException.class)
public class EncodedTrackDataManager extends AbstractDataManager<EncodedTrackBean> {

	private EncodedTrackDao i_encodedTrackDao;

	@Override
	protected DataBean getDataBean(EncodedTrackBean keyedBean) {
		return keyedBean.getTrackDataBean();
	}
	
	@Override
	protected void setDataBean(EncodedTrackBean keyedBean, DataBean dataBean) {
		keyedBean.setTrackDataBean(dataBean);
	}
	
	@Override
	protected KeyedDao<EncodedTrackBean> getDao() {
		return getEncodedTrackDao();
	}
	
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
}
