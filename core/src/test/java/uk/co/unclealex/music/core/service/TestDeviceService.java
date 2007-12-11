package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import uk.co.unclealex.music.core.dao.EncoderDao;
import uk.co.unclealex.music.core.dao.OwnerDao;
import uk.co.unclealex.music.core.model.DeviceBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.service.DeviceService;
import uk.co.unclealex.music.core.service.DeviceServiceImpl;

public class TestDeviceService extends DeviceServiceImpl implements
		DeviceService {

	private static final Map<String, String> OWNERS;
	static {
		OWNERS = new HashMap<String, String>();
		OWNERS.put("mp3", "Alex");
		OWNERS.put("ogg", "Trevor");
	}
	private EncoderDao i_encoderDao;
	private OwnerDao i_ownerDao;
	
	@Override
	public SortedMap<DeviceBean, String> findDevicesAndFiles() throws IOException {
		SortedMap<DeviceBean, String>  pathsByDeviceBean = new TreeMap<DeviceBean, String>();
		File tmp = new File(System.getProperty("java.io.tmpdir")); 
		OwnerDao ownerDao = getOwnerDao();
		boolean deletingRequired = false;
		for (EncoderBean encoderBean : getEncoderDao().getAll()) {
			String extension = encoderBean.getExtension();
			DeviceBean deviceBean = new DeviceBean();
			deviceBean.setId(encoderBean.getId());
			deviceBean.setDeletingRequired(deletingRequired = !deletingRequired);
			deviceBean.setDescription(extension + " encoder");
			deviceBean.setEncoderBean(encoderBean);
			deviceBean.setIdentifier(extension);
			deviceBean.setOwnerBean(ownerDao.findByName(OWNERS.get(extension)));
			File deviceDirectory = new File(tmp, "testdeviceserivce-" + extension);
			pathsByDeviceBean.put(deviceBean, deviceDirectory.getCanonicalPath());
		}
		return pathsByDeviceBean;
	}

	@Override
	public boolean mountingRequiresPassword(String path) {
		return false;
	}
	
	@Override
	public File getMountPointForFile(String path) throws IOException {
		File f = new File(path);
		f.mkdirs();
		return f;
	}
	
	@Override
	public void safelyRemove(File mountPoint) throws IOException {
	}
	
	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}
}
