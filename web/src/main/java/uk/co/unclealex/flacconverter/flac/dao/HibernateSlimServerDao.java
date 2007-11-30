package uk.co.unclealex.flacconverter.flac.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import uk.co.unclealex.flacconverter.flac.model.SlimServerInformationBean;

public class HibernateSlimServerDao extends HibernateDaoSupport implements
		SlimServerInformationDao {

	@Override
	public SlimServerInformationBean getSlimserverInformationByName(String name) {
		return (SlimServerInformationBean) getSession().get(SlimServerInformationBean.class, name);
	}

}
