package uk.co.unclealex.music.encoder.listener;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.hibernate.service.DataService;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.EncoderConfiguration;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.action.TrackImportedAction;
import uk.co.unclealex.music.encoder.exception.EventException;

@Transactional
public class ImportEncodingEventListener extends EncoderEncodingEventListener {

	private static Logger log = Logger.getLogger(ImportEncodingEventListener.class);
	
	private EncoderConfiguration i_encoderConfiguration;
	private Map<ImportTrackKey, File> i_fileMap;
	private EncoderDao i_encoderDao;
	private DataService i_dataService;
	
	@Override
	public void encodingStarted() {
		File importMusicDirectory = getEncoderConfiguration().getImportDirectory();
		Map<ImportTrackKey, File> fileMap = new HashMap<ImportTrackKey, File>();
		if (importMusicDirectory != null && importMusicDirectory.isDirectory()) {
			SortedSet<EncoderBean> encoderBeans = getEncoderDao().getAll();
			for (File file : importMusicDirectory.listFiles((FileFilter) FileFileFilter.FILE)) {
				ImportTrackKey importTrackKey = createKey(file, encoderBeans);
				if (importTrackKey != null) {
					log.info("File " + file + " is " + importTrackKey);
					fileMap.put(importTrackKey, file);
				}
				else {
					log.info("Could not identify file " + file);
				}
			}
		}
		setFileMap(fileMap);
		super.encodingStarted();
	}
	
	protected ImportTrackKey createKey(File file, Collection<EncoderBean> encoderBeans) {
		ImportTrackKey importTrackKey = null;
		EncoderBean encoderBean = findEncoderBean(file, encoderBeans);
		if (encoderBean == null) {
			return null;
		}
		if ("ogg".equals(encoderBean.getExtension())) {
			importTrackKey = createOggKey(file);
		}
		else if ("mp3".equals(encoderBean.getExtension())) {
			importTrackKey = createMp3Key(file);
		}
		return importTrackKey;
	}

	protected EncoderBean findEncoderBean(File file, Collection<EncoderBean> encoderBeans) {
		EncoderBean result = null;
		for (Iterator<EncoderBean> iter = encoderBeans.iterator(); result == null && iter.hasNext(); ) {
			EncoderBean encoderBean = iter.next();
			String magicNumberHex = encoderBean.getMagicNumber();
			int length = magicNumberHex.length();
			byte[] magicNumber = new byte[length / 2];
	    for (int i = 0; i < length; i += 2) {
	        magicNumber[i / 2] = 
	        	(byte) ((Character.digit(magicNumberHex.charAt(i), 16) << 4)
	                             + Character.digit(magicNumberHex.charAt(i+1), 16));
	    }
			InputStream in = null;
			try {
				in = new FileInputStream(file);
				byte[] buf = new byte[magicNumber.length];
				in.read(buf);
				if (Arrays.equals(magicNumber, buf)) {
					result = encoderBean;
				}
			}
			catch (IOException e) {
				log.warn("Could not read file " + file, e);
			}
			finally {
				IOUtils.closeQuietly(in);
			}
		}
		return result;
	}

	protected ImportTrackKey createMp3Key(File file) {
		return createKey(file, "mp3");
	}

	protected ImportTrackKey createOggKey(File file) {
		return createKey(file, "ogg");
	}

	protected ImportTrackKey createKey(File file, String extension) {
		try {
			AudioFile audioFile = AudioFileIO.read(file);
			Tag tag = audioFile.getTag();
			return new ImportTrackKey(
					tag.getFirstArtist(), tag.getFirstAlbum(), Integer.parseInt(tag.getFirstTrack()), tag.getFirstTitle(), extension);
		}
		catch (Throwable t) {
			log.warn("Could not read from file " + file, t);
			return null;
		}
	}
	
	@Override
	public void trackAdded(FlacTrackBean flacTrackBean, EncodedTrackBean encodedTrackBean, final List<EncodingAction> encodingActions) throws EventException {
		FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
		EncoderBean encoderBean = encodedTrackBean.getEncoderBean();
		String extension = encoderBean.getExtension();
		ImportTrackKey importTrackKey = 
			new ImportTrackKey(
					flacAlbumBean.getFlacArtistBean().getName(), flacAlbumBean.getTitle(), flacTrackBean.getTrackNumber(),
					flacTrackBean.getTitle(), extension);
		File file = getFileMap().get(importTrackKey);
		if (file == null) {
			log.info("Cannot find an existing file for " + flacTrackBean + " with encoder " +  extension + ". Encoding instead.");
			super.trackAdded(flacTrackBean, encodedTrackBean, encodingActions);
		}
		else if (flacTrackBean.getTimestamp() - file.lastModified() > 0) {
			log.info("File " + file + " is older than track " + flacTrackBean + ". Encoding instead");
			super.trackAdded(flacTrackBean, encodedTrackBean, encodingActions);
		}
		else {
			log.info("Using file " + file + " for track " + flacTrackBean + " and encoder " + extension);
			encodingActions.add(new TrackImportedAction(encodedTrackBean));
			FileInputStream in = null;
			FileOutputStream out = null;
			DataBean dataBean = createDataBean(flacTrackBean, encodedTrackBean);
			try {
				in = new FileInputStream(file);
				out = new FileOutputStream(getDataService().findFile(dataBean));
				out.getChannel().transferFrom(in.getChannel(), 0, file.length());
			}
			catch (IOException e) {
				throw new EventException(e);
			}
			finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}
	}
	
	@Override
	public void encodingFinished() {
		setFileMap(null);
		super.encodingFinished();
	}
	
	public Map<ImportTrackKey, File> getFileMap() {
		return i_fileMap;
	}

	public void setFileMap(Map<ImportTrackKey, File> fileMap) {
		i_fileMap = fileMap;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public EncoderConfiguration getEncoderConfiguration() {
		return i_encoderConfiguration;
	}

	public void setEncoderConfiguration(EncoderConfiguration encoderConfiguration) {
		i_encoderConfiguration = encoderConfiguration;
	}

	public DataService getDataService() {
		return i_dataService;
	}

	public void setDataService(DataService dataService) {
		i_dataService = dataService;
	}
}
