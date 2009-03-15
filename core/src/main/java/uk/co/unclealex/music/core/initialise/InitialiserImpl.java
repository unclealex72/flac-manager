package uk.co.unclealex.music.core.initialise;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.TreeSet;

import javax.jcr.RepositoryException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.hibernate.model.KeyedBean;
import uk.co.unclealex.music.base.dao.AlbumCoverDao;
import uk.co.unclealex.music.base.dao.DeviceDao;
import uk.co.unclealex.music.base.dao.EncodedAlbumDao;
import uk.co.unclealex.music.base.dao.EncodedArtistDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.initialise.Initialiser;
import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.model.SpeechProviderEnum;
import uk.co.unclealex.music.base.service.filesystem.ClearableRepositoryFactory;
import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;

@Service
@Transactional
public class InitialiserImpl implements Initialiser {

	private static final Logger log = Logger.getLogger(InitialiserImpl.class);
	
	private OwnerDao i_ownerDao;
	private EncoderDao i_encoderDao;
	private DeviceDao i_deviceDao;
	private EncodedTrackDao i_encodedTrackDao;
	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedArtistDao i_encodedArtistDao;
	private AlbumCoverDao i_albumCoverDao;
	private RepositoryManager i_coversRepositoryManager;
	private RepositoryManager i_encodedRepositoryManager;
	private ClearableRepositoryFactory i_encodedRepositoryFactory;
	private ClearableRepositoryFactory i_coversRepositoryFactory;
	
	public void initialise() throws IOException, RepositoryException {
		log.info("Initialising defaults.");
		OwnerBean alex = createOwnerBean("Alex", false);
		OwnerBean trevor = createOwnerBean("Trevor", true);
		EncoderBean mp3Encoder = createEncoderBean("mp3", "flac2mp3.sh", "494433", "audio/mpeg3");
		EncoderBean oggEncoder = createEncoderBean("ogg", "flac2ogg.sh", "4f676753", "audio/ogg");
		DeviceBean toughDrive = createDeviceBean("MHV2040AH", "ToughDrive Car HardDrive", alex, mp3Encoder, true, SpeechProviderEnum.NONE);
		DeviceBean creativeZen = createDeviceBean("ZEN", "Creative Zen Vision M", alex, mp3Encoder, true, SpeechProviderEnum.NONE);
		DeviceBean iriver120 = createDeviceBean("MK2004GAL", "iRiver", alex, oggEncoder, false, SpeechProviderEnum.ROCKBOX);
		DeviceBean iriver140 = createDeviceBean("MK2004GAH", "iRiver", trevor, oggEncoder, false, SpeechProviderEnum.ROCKBOX);
		for (OwnerBean ownerBean : new OwnerBean[] { alex, trevor }) {
			getOwnerDao().store(ownerBean);
		}
		for (EncoderBean encoderBean : new EncoderBean[] { mp3Encoder, oggEncoder }) {
			getEncoderDao().store(encoderBean);
		}
		for (DeviceBean deviceBean : new DeviceBean[] { toughDrive, creativeZen, iriver120, iriver140 }) {
			getDeviceDao().store(deviceBean);
		}
		for (
			RepositoryManager repositoryManager : 
			new RepositoryManager[] { getEncodedRepositoryManager(), getCoversRepositoryManager() }) {
			repositoryManager.updateFromScratch();
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

	protected EncoderBean createEncoderBean(String extension, String command, String magicNumber, String contentType) throws IOException {
		EncoderBean encoderBean = new EncoderBean();
		InputStream in = getClass().getResourceAsStream(command);
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer);
		in.close();
		encoderBean.setCommand(writer.toString());
		encoderBean.setExtension(extension);
		encoderBean.setMagicNumber(magicNumber);
		encoderBean.setContentType(contentType);
		encoderBean.setEncodedTrackBeans(new TreeSet<EncodedTrackBean>());
		encoderBean.setDeviceBeans(new TreeSet<DeviceBean>());
		return encoderBean;
	}
	
	private DeviceBean createDeviceBean(
			String identifier, String description, OwnerBean ownerBean, EncoderBean encoderBean, 
			boolean deletingRequired, SpeechProviderEnum speechProviderEnum) {
		DeviceBean deviceBean = new DeviceBean();
		deviceBean.setDescription(description);
		deviceBean.setIdentifier(identifier);
		deviceBean.setOwnerBean(ownerBean);
		deviceBean.setEncoderBean(encoderBean);
		deviceBean.setDeletingRequired(deletingRequired);
		deviceBean.setSpeechProviderEnum(speechProviderEnum);
		deviceBean.setTitleFormat("${1:artist}/${artist}/${album}/${2:track} - ${title}");
		ownerBean.getDeviceBeans().add(deviceBean);
		encoderBean.getDeviceBeans().add(deviceBean);
		return deviceBean;
	}

	@Override
	public void clear() throws RepositoryException {
		removeAll(getAlbumCoverDao());
		removeAll(getEncodedTrackDao());
		removeAll(getEncodedAlbumDao());
		removeAll(getEncodedArtistDao());
		removeAll(getDeviceDao());
		removeAll(getOwnerDao());
		removeAll(getEncoderDao());
		getEncodedRepositoryFactory().clearNextInstance();
		getCoversRepositoryFactory().clearNextInstance();
	}

	protected <T extends KeyedBean<T>> void removeAll(KeyedDao<T> dao) {
		for (T keyedBean : dao.getAll()) {
			dao.remove(keyedBean);
		}
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

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public EncodedAlbumDao getEncodedAlbumDao() {
		return i_encodedAlbumDao;
	}

	@Required
	public void setEncodedAlbumDao(EncodedAlbumDao encodedAlbumDao) {
		i_encodedAlbumDao = encodedAlbumDao;
	}

	public EncodedArtistDao getEncodedArtistDao() {
		return i_encodedArtistDao;
	}

	@Required
	public void setEncodedArtistDao(EncodedArtistDao encodedArtistDao) {
		i_encodedArtistDao = encodedArtistDao;
	}

	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}

	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}

	public RepositoryManager getCoversRepositoryManager() {
		return i_coversRepositoryManager;
	}

	@Required
	public void setCoversRepositoryManager(RepositoryManager coversRepositoryManager) {
		i_coversRepositoryManager = coversRepositoryManager;
	}

	public RepositoryManager getEncodedRepositoryManager() {
		return i_encodedRepositoryManager;
	}

	@Required
	public void setEncodedRepositoryManager(
			RepositoryManager encodedRepositoryManager) {
		i_encodedRepositoryManager = encodedRepositoryManager;
	}

	public ClearableRepositoryFactory getEncodedRepositoryFactory() {
		return i_encodedRepositoryFactory;
	}

	@Required
	public void setEncodedRepositoryFactory(ClearableRepositoryFactory encodedRepositoryFactory) {
		i_encodedRepositoryFactory = encodedRepositoryFactory;
	}

	public ClearableRepositoryFactory getCoversRepositoryFactory() {
		return i_coversRepositoryFactory;
	}

	@Required
	public void setCoversRepositoryFactory(ClearableRepositoryFactory coversRepositoryFactory) {
		i_coversRepositoryFactory = coversRepositoryFactory;
	}
}
