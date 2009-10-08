package uk.co.unclealex.music.encoder.listener;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.action.TrackImportedAction;
import uk.co.unclealex.music.encoder.exception.EventException;

@Transactional
public class ImportEncodingEventListener extends EncoderEncodingEventListener {

	private static Logger log = Logger.getLogger(ImportEncodingEventListener.class);
	
	private File i_importMusicDirectory;
	private Map<ImportTrackKey, File> i_fileMap;
	private EncoderDao i_encoderDao;
	
	@Override
	public void encodingStarted() {
		File importMusicDirectory = getImportMusicDirectory();
		Map<ImportTrackKey, File> fileMap = new TreeMap<ImportTrackKey, File>();
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
			String magicNumber = encoderBean.getMagicNumber();
			Reader reader = null;
			try {
				reader = new FileReader(file);
				char[] cbuf = new char[magicNumber.length()];
				reader.read(cbuf);
				if (magicNumber.equals(new String(cbuf))) {
					result = encoderBean;
				}
			}
			catch (IOException e) {
				log.warn("Could not read file " + file, e);
			}
			finally {
				IOUtils.closeQuietly(reader);
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
		String extension = encodedTrackBean.getEncoderBean().getExtension();
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
			try {
				DataBean dataBean = getDataService().createDataBean();
				in = new FileInputStream(file);
				out = new FileOutputStream(dataBean.getFile());
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

	public File getImportMusicDirectory() {
		return i_importMusicDirectory;
	}

	public void setImportMusicDirectory(File importMusicDirectory) {
		i_importMusicDirectory = importMusicDirectory;
	}
}
