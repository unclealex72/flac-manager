package uk.co.unclealex.flacconverter.flac.dao;

import java.util.Map;

import uk.co.unclealex.music.encoder.flac.dao.SlimServerInformationDao;
import uk.co.unclealex.music.encoder.flac.model.SlimServerInformationBean;

public class TestSlimServerInformationDao implements SlimServerInformationDao {

	private Map<String, Long> i_information;
	
	@Override
	public SlimServerInformationBean getSlimserverInformationByName(String name) {
		Map<String, Long> information = getInformation();
		if (information == null) {
			return null;
		}
		Long value = information.get(name);
		if (value == null) {
			return null;
		}
		SlimServerInformationBean bean = new SlimServerInformationBean();
		bean.setName(name);
		bean.setValue(value);
		return bean;
	}

	public Map<String, Long> getInformation() {
		return i_information;
	}

	public void setInformation(Map<String, Long> information) {
		i_information = information;
	}

}
