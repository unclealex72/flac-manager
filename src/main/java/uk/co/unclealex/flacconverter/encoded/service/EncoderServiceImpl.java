package uk.co.unclealex.flacconverter.encoded.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.dao.EncoderDao;
import uk.co.unclealex.flacconverter.encoded.dao.TrackDataDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodingCommandBean;
import uk.co.unclealex.flacconverter.encoded.model.TrackDataBean;
import uk.co.unclealex.flacconverter.flac.dao.FlacTrackDao;
import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;
import uk.co.unclealex.flacconverter.flac.service.SlimServerService;

public class EncoderServiceImpl implements EncoderService, Serializable {

	private transient static Logger log = Logger.getLogger(EncoderServiceImpl.class);
	
	private transient EncodedTrackDao i_encodedTrackDao;
	private transient EncoderDao i_encoderDao;
	private transient FlacTrackDao i_flacTrackDao;
	private transient TrackDataDao i_trackDataDao;
	private int i_maximumThreads = 1;
	
	private transient SlimServerService i_slimServerService;
	
	private AtomicBoolean i_atomicCurrentlyEncoding = new AtomicBoolean(false);

	@Transactional(rollbackFor=IOException.class)
	public void encode(
			EncoderBean encoderBean, File flacFile, EncodingClosure closure, Map<EncoderBean, File> commandCache) 
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
				commandFile.getCanonicalPath(), flacFile.getCanonicalPath(), tempFile.getCanonicalPath() };
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
		if (log.isDebugEnabled()) {
			log.debug("Finished " + StringUtils.join(command, ' '));
		}
	}
	
	protected File createCommandFile(EncoderBean encoderBean) throws IOException {
		File commandFile = File.createTempFile(encoderBean.getExtension(), ".sh");
		commandFile.deleteOnExit();
		commandFile.setExecutable(true);
		FileWriter writer = new FileWriter(commandFile);
		IOUtils.copy(new StringReader(encoderBean.getCommand()), writer);
		writer.close();
		return commandFile;
	}

	public boolean encode(EncodingCommandBean encodingCommandBean, Map<EncoderBean, File> commandCache) throws IOException {
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
		String url = flacTrackBean.getUrl();
		File flacFile = flacTrackBean.getFile();
		EncodedTrackBean encodedTrackBean = encodedTrackDao.findByUrlAndEncoderBean(url, encoderBean);
		// We encode if there was no previously encoded track or the encoded track is older than the flac track
		if (encodedTrackBean == null || encodedTrackBean.getTimestamp() < flacFile.lastModified()) {
			try {
				final TrackDataDao trackDataDao = getTrackDataDao();
				final EncodedTrackBean newEncodedTrackBean = encodedTrackBean==null?new EncodedTrackBean():encodedTrackBean;
				TrackDataBean oldTrackDataBean = encodedTrackBean==null?null:encodedTrackBean.getTrackDataBean();
				final TrackDataBean newTrackDataBean = new TrackDataBean();
				newEncodedTrackBean.setFlacUrl(url);
				newEncodedTrackBean.setEncoderBean(encoderBean);
				newEncodedTrackBean.setTimestamp(new Date().getTime());
				EncodingClosure closure = new EncodingClosure() {
					public void process(InputStream in) throws IOException {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						int length = IOUtils.copy(in, out);
						newTrackDataBean.setTrack(out.toByteArray());
						newTrackDataBean.setLength(length);
						newEncodedTrackBean.setTrackDataBean(newTrackDataBean);
					}
				};
				encode(encoderBean, flacFile, closure, commandCache);
				encodedTrackDao.store(newEncodedTrackBean);
				encodedTrackDao.flush();
				if (oldTrackDataBean != null) {
					trackDataDao.remove(oldTrackDataBean);
					trackDataDao.dismiss(newTrackDataBean);
				}
				encodedTrackDao.dismiss(newEncodedTrackBean);
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
			return true;
		}
		else {
			if (log.isDebugEnabled()) {
				log.debug(
						formatter.format(
								"Skipping %s %s, %s: %02d - %s", extension, artistName, albumName, trackNumber, trackName));
			}
			return false;
		}
	}

	@Override
	public int encodeAll()
	throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException {
		return encodeAll(getMaximumThreads());
	}
	
	public int encodeAll(int maximumThreads)
	throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException {
		if (getSlimServerService().isScanning()) {
			throw new CurrentlyScanningException();
		}
		if (!getAtomicCurrentlyEncoding().compareAndSet(false, true)) {
			throw new AlreadyEncodingException();
		}
		final SortedMap<EncodingCommandBean, Throwable> errors =
			Collections.synchronizedSortedMap(new TreeMap<EncodingCommandBean, Throwable>());
		final SortedMap<EncoderBean, File> commandCache = new TreeMap<EncoderBean, File>();
		final BlockingQueue<EncodingCommandBean> encodingCommandBeans = new LinkedBlockingQueue<EncodingCommandBean>();
		EncodingWorker[] workers = new EncodingWorker[maximumThreads];
		for (int idx = 0; idx < maximumThreads; idx++) {
			workers[idx] = new EncodingWorker(encodingCommandBeans, errors) {
				@Override
				protected void process(EncodingCommandBean encodingCommandBean) throws IOException {
					encode(encodingCommandBean, commandCache);
				}
			};
			workers[idx].start();
		}
		SortedSet<EncoderBean> allEncoderBeans = getEncoderDao().getAll();
		for (EncoderBean encoderBean : allEncoderBeans) {
			commandCache.put(encoderBean, createCommandFile(encoderBean));
		}
		for (FlacTrackBean flacTrackBean : getFlacTrackDao().getAll()) {
			for (EncoderBean encoderBean : allEncoderBeans) {
				encodingCommandBeans.offer(new EncodingCommandBean(encoderBean, flacTrackBean));
			}
		}
		for (EncodingWorker worker : workers) {
			encodingCommandBeans.offer(worker.getEndOfWorkBean());
		}
		int totalCount = 0;
		for (EncodingWorker worker : workers) {
			try {
				worker.join();
			}
			catch (InterruptedException e) {
				// Do nothing
			}
			totalCount += worker.getCount();
		}
		
		for (File command : commandCache.values()) {
			command.delete();
		}
		getAtomicCurrentlyEncoding().set(false);
		if (!errors.isEmpty()) {
			throw new MultipleEncodingException(errors, totalCount);
		}
		return totalCount;
	}

	public int removeDeleted() {
		final SortedSet<String> urls = new TreeSet<String>();
		CollectionUtils.collect(
			getFlacTrackDao().getAll(),
			new Transformer<FlacTrackBean, String>() {
				public String transform(FlacTrackBean flacTrackBean) {
					return flacTrackBean.getUrl();
				}
			},
			urls);
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		SortedSet<EncodedTrackBean> extraTracks = new TreeSet<EncodedTrackBean>();
		extraTracks.addAll(
			CollectionUtils.selectRejected(
				encodedTrackDao.getAll(),
				new Predicate<EncodedTrackBean>() {
					public boolean evaluate(EncodedTrackBean encodedTrackBean) {
						return urls.contains(encodedTrackBean.getFlacUrl());
					}
				}));
		for (EncodedTrackBean encodedTrackBean : extraTracks) {
			encodedTrackDao.remove(encodedTrackBean);
			log.info(
					"Removed " + encodedTrackBean.getEncoderBean().getExtension() +
					" conversion of " + encodedTrackBean.getFlacUrl());
		}
		return extraTracks.size();
	}
	
	public boolean isCurrentlyEncoding() {
		return getAtomicCurrentlyEncoding().get();
	}
	
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	public AtomicBoolean getAtomicCurrentlyEncoding() {
		return i_atomicCurrentlyEncoding;
	}

	public void setAtomicCurrentlyEncoding(AtomicBoolean atomicCurrentlyEncoding) {
		i_atomicCurrentlyEncoding = atomicCurrentlyEncoding;
	}

	public SlimServerService getSlimServerService() {
		return i_slimServerService;
	}

	public void setSlimServerService(SlimServerService slimServerService) {
		i_slimServerService = slimServerService;
	}

	public TrackDataDao getTrackDataDao() {
		return i_trackDataDao;
	}

	public void setTrackDataDao(TrackDataDao trackDataDao) {
		i_trackDataDao = trackDataDao;
	}

	public int getMaximumThreads() {
		return i_maximumThreads;
	}

	public void setMaximumThreads(int maximumThreads) {
		i_maximumThreads = maximumThreads;
	}
}
