package uk.co.unclealex.music.encoder.flac.dao;

import uk.co.unclealex.music.encoder.flac.model.SlimServerInformationBean;

public interface SlimServerInformationDao {

	public SlimServerInformationBean getSlimserverInformationByName(String name);
}
