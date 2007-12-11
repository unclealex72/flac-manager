package uk.co.unclealex.flacconverter.flac.service;

import java.util.List;

import uk.co.unclealex.flacconverter.SlimServerService;
import uk.co.unclealex.flacconverter.flac.dao.SlimServerInformationDao;
import uk.co.unclealex.flacconverter.flac.model.SlimServerInformationBean;
import uk.co.unclealex.music.core.SlimServerConfig;

public class SlimServerServiceImpl implements SlimServerService {

	protected static String IS_SCANNING = "isScanning";
	private SlimServerInformationDao i_slimServerInformationDao;
	private SlimServerConfig i_slimServerConfig;
	
	@Override
	public boolean isScanning() {
		Long value = getValue(IS_SCANNING);
		return value != null && value != 0;
	}

	public List<String> getDefiniteArticles() {
		return getSlimServerConfig().getDefiniteArticles();
	}
	
	private Long getValue(String name) {
		SlimServerInformationBean slimServerInformationBean =
			getSlimServerInformationDao().getSlimserverInformationByName(name);
		return slimServerInformationBean==null?null:slimServerInformationBean.getValue();
	}

	public SlimServerInformationDao getSlimServerInformationDao() {
		return i_slimServerInformationDao;
	}

	public void setSlimServerInformationDao(
			SlimServerInformationDao slimServerInformationDao) {
		i_slimServerInformationDao = slimServerInformationDao;
	}

	public SlimServerConfig getSlimServerConfig() {
		return i_slimServerConfig;
	}

	public void setSlimServerConfig(SlimServerConfig slimServerConfig) {
		i_slimServerConfig = slimServerConfig;
	}

}
