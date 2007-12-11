package uk.co.unclealex.music.core.encoded.service;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import uk.co.unclealex.flacconverter.encoded.service.TitleFormatFactory;
import uk.co.unclealex.music.core.encoded.dao.DeviceDao;
import uk.co.unclealex.music.core.encoded.dao.EncoderDao;
import uk.co.unclealex.music.core.encoded.dao.OwnerDao;
import uk.co.unclealex.music.core.encoded.model.DeviceBean;

public class FileSystemDeviceService extends DeviceServiceImpl {

	private SortedMap<DeviceBean, String> i_pathsByDeviceBean;
	private EncoderDao i_encoderDao;
	private OwnerDao i_ownerDao;
	private TitleFormatFactory i_titleFormatFactory;
	
	public void initialise() throws IOException {
		setPathsByDeviceBean(new TreeMap<DeviceBean, String>());
		createDeviceBean(false, "Dummy", "mp3", "mp3device");
		createDeviceBean(false, "Dummy", "ogg", "oggdevice");
	}
	
	protected void createDeviceBean(
			boolean  deletingRequired, String ownerName, String encoderExtension, String path) throws IOException {
		File f = new File("/home/alex/flac", path);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				throw new IOException("Cannot create directory " + f);
			}
		}
		else {
			if (!f.isDirectory()) {
				throw new IOException(f + " already exists and is not a directory.");
			}
		}
		DeviceBean deviceBean = new DeviceBean();
		deviceBean.setDeletingRequired(deletingRequired);
		deviceBean.setDescription("filesystem at " + f);
		deviceBean.setIdentifier(path);
		deviceBean.setTitleFormat(getTitleFormatFactory().getDefaultTitleFormat());
		deviceBean.setOwnerBean(getOwnerDao().findByName(ownerName));
		deviceBean.setEncoderBean(getEncoderDao().findByExtension(encoderExtension));
		SortedMap<DeviceBean, String> pathsByDeviceBean = getPathsByDeviceBean();
		pathsByDeviceBean.put(deviceBean, f.getPath());
		deviceBean.setId(pathsByDeviceBean.size());
	}
	
	@Override
	public SortedMap<DeviceBean, String> findDevicesAndFiles() throws IOException {
		return getPathsByDeviceBean();
	}

	@Override
	public File getMountPointForFile(String path) throws IOException {
		return new File(path).getCanonicalFile();
	}

	@Override
	public boolean mountingRequiresPassword(String path) {
		return false;
	}

	@Override
	public void safelyRemove(File mountPoint) throws IOException {
	}

	public DeviceDao createDeviceDao() {
		return new DeviceDao() {
			public DeviceBean findById(final int id) {
				return CollectionUtils.find(
						getPathsByDeviceBean().keySet(),
						new Predicate<DeviceBean>() {
							@Override
							public boolean evaluate(DeviceBean deviceBean) {
								return new Integer(id).equals(deviceBean.getId());
							}
						});
			}
			@Override
			public SortedSet<DeviceBean> getAll() {
				return new TreeSet<DeviceBean>(getPathsByDeviceBean().keySet());
			}
			@Override
			public void remove(DeviceBean keyedBean) {
			}
			@Override
			public void store(DeviceBean keyedBean) {
			}
			@Override
			public void clear() {
			}
			@Override
			public void dismiss(DeviceBean keyedBean) {
			}
			@Override
			public void flush() {
			}			
		};
	}
	
	public SortedMap<DeviceBean, String> getPathsByDeviceBean() {
		return i_pathsByDeviceBean;
	}

	public void setPathsByDeviceBean(SortedMap<DeviceBean, String> pathsByDeviceBean) {
		i_pathsByDeviceBean = pathsByDeviceBean;
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

	public TitleFormatFactory getTitleFormatFactory() {
		return i_titleFormatFactory;
	}

	public void setTitleFormatFactory(TitleFormatFactory titleFormatFactory) {
		i_titleFormatFactory = titleFormatFactory;
	}

}
