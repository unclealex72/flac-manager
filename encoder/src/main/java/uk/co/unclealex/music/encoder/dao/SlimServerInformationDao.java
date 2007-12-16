package uk.co.unclealex.music.encoder.dao;

import uk.co.unclealex.music.encoder.model.SlimServerInformationBean;

public interface SlimServerInformationDao {

	public SlimServerInformationBean getSlimserverInformationByName(String name);
}
