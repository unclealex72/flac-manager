package uk.co.unclealex.music.core.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.SlimServerInformationDao;
import uk.co.unclealex.music.base.model.SlimServerInformationBean;

@Transactional
public class HibernateSlimServerInformationDao extends HibernateDaoSupport implements
		SlimServerInformationDao {

	@Override
	public SlimServerInformationBean getSlimserverInformationByName(String name) {
		return (SlimServerInformationBean) getSession().get(SlimServerInformationBean.class, name);
	}

}
