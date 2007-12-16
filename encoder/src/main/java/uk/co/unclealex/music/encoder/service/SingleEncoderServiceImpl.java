package uk.co.unclealex.music.encoder.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.dao.TrackDataDao;
import uk.co.unclealex.music.core.io.SequenceOutputStream;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.model.TrackDataBean;
import uk.co.unclealex.music.core.service.TrackDataStreamIteratorFactory;
import uk.co.unclealex.music.core.service.TrackStreamService;
import uk.co.unclealex.music.encoder.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.model.FlacTrackBean;

@Service
@Transactional
public class SingleEncoderServiceImpl implements SingleEncoderService, Serializable {

	private static Logger log = Logger.getLogger(SingleEncoderServiceImpl.class);

	private EncodedTrackDao i_encodedTrackDao;
	private TrackDataDao i_trackDataDao;
	private FlacTrackService i_flactrackService;
	private TrackStreamService i_trackStreamService;
	private TrackDataStreamIteratorFactory i_trackDataStreamIteratorFactory;
	
	@Transactional(rollbackFor=IOException.class)
	public int encode(
			EncoderBean encoderBean, FlacTrackBean flacTrackBean, EncodingClosure closure, Map<EncoderBean, File> commandCache) 
	throws IOException {
		File commandFile = commandCache.get(encoderBean);
		if (commandFile == null) {
			commandFile = createCommandFile(encoderBean);
			commandCache.put(encoderBean, commandFile);
		}
		File tempFile = File.createTempFile("encoding", "." + encoderBean.getExtension());
		tempFile.deleteOnExit();
		
		String[] command =
			new String[] { 
				commandFile.getCanonicalPath(), flacTrackBean.getFile().getCanonicalPath(), tempFile.getCanonicalPath() };
		if (log.isDebugEnabled()) {
			log.debug("Running " + StringUtils.join(command, ' '));
		}
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		int returnValue;
		InputStream in = null;
		try {
			try {
				returnValue = process.waitFor();
				if (returnValue != 0) {
					StringWriter error = new StringWriter();
					IOUtils.copy(process.getErrorStream(), error);
					throw new IOException(
							"The process " + StringUtils.join(command, ' ') + " failed with exit code " + returnValue + "\n" + error);
				}
				in = new FileInputStream(tempFile);
				closure.process(in);
				if (log.isDebugEnabled()) {
					log.debug("Finished " + StringUtils.join(command, ' '));
				}
				return (int) tempFile.length();
			}
			catch (InterruptedException e) {
				throw new IOException(
						"The process " + StringUtils.join(command, ' ') + " was interrupted.", e);
			}
		}
		finally {
			IOUtils.closeQuietly(in);
			tempFile.delete();
		}
	}

	@Transactional(rollbackFor=IOException.class)
	public File createCommandFile(EncoderBean encoderBean) throws IOException {
		File commandFile = File.createTempFile(encoderBean.getExtension(), ".sh");
		commandFile.deleteOnExit();
		commandFile.setExecutable(true);
		FileWriter writer = new FileWriter(commandFile);
		IOUtils.copy(new StringReader(encoderBean.getCommand()), writer);
		writer.close();
		return commandFile;
	}

	@Transactional(rollbackFor=IOException.class)
	public EncodedTrackBean encode(EncodingCommandBean encodingCommandBean, Map<EncoderBean, File> commandCache) throws IOException {
		TrackDataDao trackDataDao = getTrackDataDao();
		FlacTrackService flactrackService = getFlactrackService();

		EncoderBean encoderBean = encodingCommandBean.getEncoderBean();
		FlacTrackBean flacTrackBean = encodingCommandBean.getFlacTrackBean();
		
		final EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		final TrackDataStreamIteratorFactory trackDataStreamIteratorFactory = getTrackDataStreamIteratorFactory();
		FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
		final String albumName = flacAlbumBean.getTitle();
		final String artistName = flacAlbumBean.getFlacArtistBean().getName();
		final String trackName = flacTrackBean.getTitle();
		final int trackNumber = flacTrackBean.getTrackNumber();
		final String extension = encoderBean.getExtension();
		
		final Formatter formatter = new Formatter();
		EncodedTrackBean retval = null;
		String url = flacTrackBean.getUrl();
		EncodedTrackBean encodedTrackBean = encodedTrackDao.findByUrlAndEncoderBean(url, encoderBean);
		// We encode if there was no previously encoded track or the encoded track is older than the flac track
		if (encodedTrackBean == null || encodedTrackBean.getTimestamp() < flacTrackBean.getFile().lastModified()) {
			final EncodedTrackBean newEncodedTrackBean = encodedTrackBean==null?new EncodedTrackBean():encodedTrackBean;
			EncodedAlbumBean encodedAlbumBean = flactrackService.findOrCreateEncodedAlbumBean(flacAlbumBean);
			newEncodedTrackBean.setFlacUrl(url);
			newEncodedTrackBean.setEncoderBean(encoderBean);
			newEncodedTrackBean.setTimestamp(new Date().getTime());
			newEncodedTrackBean.setLength(-1);
			newEncodedTrackBean.setEncodedAlbumBean(encodedAlbumBean);
			SortedSet<TrackDataBean> trackDataBeans = newEncodedTrackBean.getTrackDataBeans();
			if (trackDataBeans != null) {
				for (TrackDataBean trackDataBean : trackDataBeans) {
					trackDataDao.remove(trackDataBean);
				}
			}
			newEncodedTrackBean.setTrackDataBeans(new TreeSet<TrackDataBean>());
			encodedTrackDao.store(newEncodedTrackBean);
			try {	
				EncodingClosure closure = new EncodingClosure() {
					public void process(InputStream in) throws IOException {
						Iterator<OutputStream> outIterator = 
							trackDataStreamIteratorFactory.createTrackDataOutputStreamIterator(newEncodedTrackBean);
						OutputStream out = 
							new SequenceOutputStream(getTrackStreamService().getMaximumTrackDataLength(), outIterator);
						int length = IOUtils.copy(in, out);
						out.close();
						newEncodedTrackBean.setLength(length);
					}
				};
				encode(encoderBean, flacTrackBean, closure, commandCache);
				encodedTrackDao.store(newEncodedTrackBean);
				encodedTrackDao.flush();
				encodedTrackDao.dismiss(newEncodedTrackBean);
				retval = newEncodedTrackBean;
			}
			catch (IOException e) {
				log.error(
					formatter.format(
						"Error converting %s %s, %s: %02d - %s", extension, artistName, albumName, trackNumber, trackName),
					e);
				throw e;
			}
			catch (RuntimeException e) {
				log.error(
					formatter.format(
						"Error converting %s %s, %s: %02d - %s", extension, artistName, albumName, trackNumber, trackName),
					e);
				throw e;
			}
			log.info(
					formatter.format(
							"Converted %s %s, %s: %02d - %s", extension, artistName, albumName, trackNumber, trackName));
		}
		else if (log.isDebugEnabled()) {
			log.debug(
					formatter.format(
							"Skipping %s %s, %s: %02d - %s", extension, artistName, albumName, trackNumber, trackName));
		}
		return retval;
	}
	
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public TrackDataDao getTrackDataDao() {
		return i_trackDataDao;
	}

	@Required
	public void setTrackDataDao(TrackDataDao trackDataDao) {
		i_trackDataDao = trackDataDao;
	}

	public TrackDataStreamIteratorFactory getTrackDataStreamIteratorFactory() {
		return i_trackDataStreamIteratorFactory;
	}

	@Required
	public void setTrackDataStreamIteratorFactory(
			TrackDataStreamIteratorFactory trackDataStreamIteratorFactory) {
		i_trackDataStreamIteratorFactory = trackDataStreamIteratorFactory;
	}

	public TrackStreamService getTrackStreamService() {
		return i_trackStreamService;
	}

	@Required
	public void setTrackStreamService(TrackStreamService trackStreamService) {
		i_trackStreamService = trackStreamService;
	}

	public FlacTrackService getFlactrackService() {
		return i_flactrackService;
	}

	@Required
	public void setFlactrackService(FlacTrackService flactrackService) {
		i_flactrackService = flactrackService;
	}
}
