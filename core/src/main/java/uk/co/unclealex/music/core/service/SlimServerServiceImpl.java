package uk.co.unclealex.music.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.base.SlimServerConfig;
import uk.co.unclealex.music.base.dao.SlimServerInformationDao;
import uk.co.unclealex.music.base.model.SlimServerInformationBean;

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
	
	@Override
	public String makePathAbsolute(String path) {
		String absolutePath = getSlimServerConfig().getRootDirectory() + "/" + path;
		absolutePath = absolutePath.replaceAll("//+", "/");
		return absolutePath;
	}
	
	@Override
	public String makePathRelative(String path) {
		String root = getSlimServerConfig().getRootDirectory();
		String relativePath;
		if (path.startsWith(root)) {
			relativePath = path.substring(root.length());
			if (relativePath.startsWith("/")) {
				relativePath = relativePath.substring(1);
			}
		}
		else {
			relativePath = null;
		}
		return relativePath;
	}
	
	private Long getValue(String name) {
		SlimServerInformationBean slimServerInformationBean =
			getSlimServerInformationDao().getSlimserverInformationByName(name);
		return slimServerInformationBean==null?null:slimServerInformationBean.getValue();
	}

	public SlimServerInformationDao getSlimServerInformationDao() {
		return i_slimServerInformationDao;
	}

	@Required
	public void setSlimServerInformationDao(
			SlimServerInformationDao slimServerInformationDao) {
		i_slimServerInformationDao = slimServerInformationDao;
	}

	public SlimServerConfig getSlimServerConfig() {
		return i_slimServerConfig;
	}

	@Required
	public void setSlimServerConfig(SlimServerConfig slimServerConfig) {
		i_slimServerConfig = slimServerConfig;
	}

}