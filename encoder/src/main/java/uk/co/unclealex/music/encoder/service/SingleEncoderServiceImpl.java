package uk.co.unclealex.music.encoder.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Formatter;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.io.DataInjector;
import uk.co.unclealex.music.core.io.KnownLengthInputStream;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.model.FlacAlbumBean;
import uk.co.unclealex.music.core.model.FlacTrackBean;
import uk.co.unclealex.music.core.service.EncodedService;
import uk.co.unclealex.music.encoder.io.DeleteOnClosingFileInputStream;

@Service
@Transactional(propagation=Propagation.REQUIRES_NEW)
public class SingleEncoderServiceImpl implements SingleEncoderService, Serializable {

	private static Logger log = Logger.getLogger(SingleEncoderServiceImpl.class);

	private EncodedTrackDao i_encodedTrackDao;
	private EncodedService i_encodedService;
	private DataInjector<EncodedTrackBean> i_encodedTrackDataInjector;
	
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
		KnownLengthInputStream in = null;
		try {
			try {
				returnValue = process.waitFor();
				if (returnValue != 0) {
					StringWriter error = new StringWriter();
					IOUtils.copy(process.getErrorStream(), error);
					error.write("\n");
					IOUtils.copy(process.getInputStream(), error);
					throw new IOException(
							"The process " + StringUtils.join(command, ' ') + " failed with exit code " + returnValue + "\n" + error);
				}
				in = new KnownLengthInputStream(new DeleteOnClosingFileInputStream(tempFile), (int) tempFile.length());
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
		EncoderBean encoderBean = encodingCommandBean.getEncoderBean();
		FlacTrackBean flacTrackBean = encodingCommandBean.getFlacTrackBean();
		
		final EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
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
			newEncodedTrackBean.setFlacUrl(url);
			newEncodedTrackBean.setEncoderBean(encoderBean);
			newEncodedTrackBean.setTimestamp(new Date().getTime());
			newEncodedTrackBean.setTitle(trackName);
			newEncodedTrackBean.setTrackNumber(trackNumber);
			getEncodedService().injectFilename(newEncodedTrackBean);
			try {	
				EncodingClosure closure = new EncodingClosure() {
					public void process(KnownLengthInputStream in) throws IOException {
						getEncodedTrackDataInjector().injectData(newEncodedTrackBean, in);
						encodedTrackDao.store(newEncodedTrackBean);
						// Make triply sure that the new track bean is fully persisted so the encoded file can be safely deleted.
						encodedTrackDao.flush();
						encodedTrackDao.dismiss(newEncodedTrackBean);
						encodedTrackDao.findById(newEncodedTrackBean.getId());
					}
				};
				encode(encoderBean, flacTrackBean, closure, commandCache);
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

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	@Required
	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}

	public DataInjector<EncodedTrackBean> getEncodedTrackDataInjector() {
		return i_encodedTrackDataInjector;
	}

	@Required
	public void setEncodedTrackDataInjector(
			DataInjector<EncodedTrackBean> encodedTrackDataInjector) {
		i_encodedTrackDataInjector = encodedTrackDataInjector;
	}
}
