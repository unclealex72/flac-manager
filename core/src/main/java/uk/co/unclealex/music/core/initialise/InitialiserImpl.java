package uk.co.unclealex.music.core.initialise;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.DeviceDao;
import uk.co.unclealex.music.core.dao.EncoderDao;
import uk.co.unclealex.music.core.dao.KeyedDao;
import uk.co.unclealex.music.core.dao.OwnerDao;
import uk.co.unclealex.music.core.model.DeviceBean;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.model.KeyedBean;
import uk.co.unclealex.music.core.model.OwnerBean;

@Service
@Transactional
public class InitialiserImpl implements Initialiser {

	private static final Logger log = Logger.getLogger(InitialiserImpl.class);
	
	private OwnerDao i_ownerDao;
	private EncoderDao i_encoderDao;
	private DeviceDao i_deviceDao;
	
	public void initialise() throws IOException {
		log.info("Initialising defaults.");
		OwnerBean alex = createOwnerBean("Alex", false);
		OwnerBean trevor = createOwnerBean("Trevor", true);
		EncoderBean mp3Encoder = createEncoderBean("mp3", "flac2mp3.sh", "494433");
		EncoderBean oggEncoder = createEncoderBean("ogg", "flac2ogg.sh", "4f676753");
		DeviceBean toughDrive = createDeviceBean("MHV2040AH", "ToughDrive Car HardDrive", alex, mp3Encoder);
		DeviceBean iriver120 = createDeviceBean("MK2004GAL", "iRiver", alex, oggEncoder);
		DeviceBean iriver140 = createDeviceBean("MK2004GAH", "iRiver", trevor, oggEncoder);
		for (OwnerBean ownerBean : new OwnerBean[] { alex, trevor }) {
			getOwnerDao().store(ownerBean);
		}
		for (EncoderBean encoderBean : new EncoderBean[] { mp3Encoder, oggEncoder }) {
			getEncoderDao().store(encoderBean);
		}
		for (DeviceBean deviceBean : new DeviceBean[] { toughDrive, iriver120, iriver140 }) {
			getDeviceDao().store(deviceBean);
		}
	}

	protected OwnerBean createOwnerBean(String name, boolean ownsAll) {
		OwnerBean ownerBean = new OwnerBean();
		ownerBean.setName(name);
		ownerBean.setOwnsAll(ownsAll);
		ownerBean.setDeviceBeans(new TreeSet<DeviceBean>());
		ownerBean.setEncodedAlbumBeans(new TreeSet<EncodedAlbumBean>());
		ownerBean.setEncodedArtistBeans(new TreeSet<EncodedArtistBean>());
		return ownerBean;
	}

	protected EncoderBean createEncoderBean(String extension, String command, String magicNumber) throws IOException {
		EncoderBean encoderBean = new EncoderBean();
		InputStream in = getClass().getResourceAsStream(command);
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer);
		in.close();
		encoderBean.setCommand(writer.toString());
		encoderBean.setExtension(extension);
		encoderBean.setMagicNumber(magicNumber);
		encoderBean.setEncodedTrackBeans(new TreeSet<EncodedTrackBean>());
		encoderBean.setDeviceBeans(new TreeSet<DeviceBean>());
		return encoderBean;
	}
	
	private DeviceBean createDeviceBean(
			String identifier, String description, OwnerBean ownerBean, EncoderBean encoderBean) {
		DeviceBean deviceBean = new DeviceBean();
		deviceBean.setDescription(description);
		deviceBean.setIdentifier(identifier);
		deviceBean.setOwnerBean(ownerBean);
		deviceBean.setEncoderBean(encoderBean);
		deviceBean.setTitleFormat("${1:artist}/${artist}/${album}/${2:track} - ${title}");
		ownerBean.getDeviceBeans().add(deviceBean);
		encoderBean.getDeviceBeans().add(deviceBean);
		return deviceBean;
	}

	public DeviceDao getDeviceDao() {
		return i_deviceDao;
	}

	@Required
	public void setDeviceDao(DeviceDao deviceDao) {
		i_deviceDao = deviceDao;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	@Required
	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	@Required
	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

	@SuppressWarnings("unchecked")
	public void clear() {
		for (KeyedDao keyedDao : new KeyedDao[] { getEncoderDao(), getDeviceDao(), getOwnerDao() }) {
			for (Object obj : keyedDao.getAll()) {
				keyedDao.remove((KeyedBean) obj);
			}
		}
	}
}
