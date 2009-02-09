package uk.co.unclealex.music.core.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import uk.co.unclealex.music.base.dao.SlimServerInformationDao;
import uk.co.unclealex.music.base.model.SlimServerInformationBean;

@Repository
public class HibernateSlimServerInformationDao extends HibernateDaoSupport implements
		SlimServerInformationDao {

	@Autowired
	public HibernateSlimServerInformationDao(@Qualifier("flacSessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	@Override
	public SlimServerInformationBean getSlimserverInformationByName(String name) {
		return (SlimServerInformationBean) getSession().get(SlimServerInformationBean.class, name);
	}

}
