package uk.co.unclealex.music.encoder.flac.service;

import java.util.List;

import uk.co.unclealex.music.core.SlimServerConfig;
import uk.co.unclealex.music.core.SlimServerService;
import uk.co.unclealex.music.encoder.flac.dao.SlimServerInformationDao;
import uk.co.unclealex.music.encoder.flac.model.SlimServerInformationBean;

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
