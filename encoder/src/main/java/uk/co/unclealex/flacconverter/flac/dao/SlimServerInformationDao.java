package uk.co.unclealex.flacconverter.flac.dao;

import uk.co.unclealex.music.encoder.flac.model.SlimServerInformationBean;

public interface SlimServerInformationDao {

	public SlimServerInformationBean getSlimserverInformationByName(String name);
}
