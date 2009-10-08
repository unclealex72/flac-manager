package uk.co.unclealex.music.core.initialise;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.KeyedDao;
import uk.co.unclealex.hibernate.model.KeyedBean;
import uk.co.unclealex.music.base.dao.AlbumCoverDao;
import uk.co.unclealex.music.base.dao.EncodedAlbumDao;
import uk.co.unclealex.music.base.dao.EncodedArtistDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.dao.FileDao;
import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.initialise.Initialiser;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.OwnerBean;

@Transactional
public class InitialiserImpl implements Initialiser {

	private static final Logger log = Logger.getLogger(InitialiserImpl.class);
	
	private File i_encodedMusicStorageDirectory;
	private OwnerDao i_ownerDao;
	private EncoderDao i_encoderDao;
	private EncodedTrackDao i_encodedTrackDao;
	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedArtistDao i_encodedArtistDao;
	private AlbumCoverDao i_albumCoverDao;
	private FileDao i_fileDao;
	
	public void initialise() throws IOException {
		log.info("Initialising defaults.");
		clear();
		File encodedMusicStorageDirectory = getEncodedMusicStorageDirectory();
		if (encodedMusicStorageDirectory.exists()) {
			if (encodedMusicStorageDirectory.isFile()) {
				encodedMusicStorageDirectory.delete();
			}
			else {
				FileUtils.deleteDirectory(encodedMusicStorageDirectory);
			}
		}
		encodedMusicStorageDirectory.mkdirs();
		OwnerBean alex = createOwnerBean("Alex");
		OwnerBean trevor = createOwnerBean("Trevor");
		EncoderBean mp3Encoder = createEncoderBean("mp3", "flac2mp3.sh", "494433", "audio/mpeg3");
		EncoderBean oggEncoder = createEncoderBean("ogg", "flac2ogg.sh", "4f676753", "audio/ogg");
		for (OwnerBean ownerBean : new OwnerBean[] { alex, trevor }) {
			getOwnerDao().store(ownerBean);
		}
		for (EncoderBean encoderBean : new EncoderBean[] { mp3Encoder, oggEncoder }) {
			getEncoderDao().store(encoderBean);
		}
	}

	protected OwnerBean createOwnerBean(String name) {
		OwnerBean ownerBean = new OwnerBean();
		ownerBean.setName(name);
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
		return encoderBean;
	}
	
	@Override
	public void clear() {
		removeAll(getAlbumCoverDao());
		removeAll(getEncodedTrackDao());
		removeAll(getEncodedAlbumDao());
		removeAll(getEncodedArtistDao());
		removeAll(getEncoderDao());
		removeAll(getFileDao());
		removeAll(getOwnerDao());
	}

	protected <T extends KeyedBean<T>> void removeAll(KeyedDao<T> dao) {
		for (T keyedBean : dao.getAll()) {
			dao.remove(keyedBean);
		}
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

	public FileDao getFileDao() {
		return i_fileDao;
	}

	public void setFileDao(FileDao fileDao) {
		i_fileDao = fileDao;
	}

	public File getEncodedMusicStorageDirectory() {
		return i_encodedMusicStorageDirectory;
	}

	public void setEncodedMusicStorageDirectory(File encodedMusicStorageDirectory) {
		i_encodedMusicStorageDirectory = encodedMusicStorageDirectory;
	}
}
