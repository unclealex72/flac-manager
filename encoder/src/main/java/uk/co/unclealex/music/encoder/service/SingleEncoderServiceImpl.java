package uk.co.unclealex.music.encoder.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DelegateFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.io.DataInjector;
import uk.co.unclealex.music.base.io.KnownLengthInputStream;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.EncodedService;
import uk.co.unclealex.music.base.service.FlacService;
import uk.co.unclealex.music.base.service.OwnerService;
import uk.co.unclealex.music.encoder.io.DeleteOnClosingFileInputStream;

@Service
@Transactional
public class SingleEncoderServiceImpl implements SingleEncoderService, Serializable {

	private static Logger log = Logger.getLogger(SingleEncoderServiceImpl.class);

	private EncodedTrackDao i_encodedTrackDao;
	private EncodedService i_encodedService;
	private DataInjector<EncodedTrackBean> i_encodedTrackDataInjector;
	private SlimServerService i_slimServerService;
	private EncoderDao i_encoderDao;
	private FlacTrackDao i_flacTrackDao;
	private SingleEncoderService i_singleEncoderService;
	private FlacService i_flacService;
	private OwnerService i_ownerService;
	
	@Transactional(rollbackFor=IOException.class)
	public int encode(
			EncoderBean encoderBean, FlacTrackBean flacTrackBean, EncodingClosure closure, Map<String, File> commandCache) 
	throws IOException {
		File commandFile = commandCache.get(encoderBean.getExtension());
		if (commandFile == null) {
			commandFile = createCommandFile(encoderBean);
			commandCache.put(encoderBean.getExtension(), commandFile);
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
				DeleteOnClosingFileInputStream deleteOnClosingFileInputStream = new DeleteOnClosingFileInputStream(tempFile);
				in = new KnownLengthInputStream(
						deleteOnClosingFileInputStream, deleteOnClosingFileInputStream.getChannel(), (int) tempFile.length());
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
	public EncodedTrackBean encode(EncodingCommandBean encodingCommandBean, Map<String, File> commandCache) throws IOException {
		EncoderBean encoderBean = getEncoderDao().findByExtension(encodingCommandBean.getExtension());
		FlacTrackBean flacTrackBean = getFlacTrackDao().findByUrl(encodingCommandBean.getUrl());
		
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
						getEncodedTrackDao().store(newEncodedTrackBean);
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
	
	@Override
	public int removeDeleted(Collection<EncodingEventListener> encodingEventListeners) {
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
			for (EncodingEventListener encodingEventListener : encodingEventListeners) {
				encodingEventListener.beforeTrackRemoved(encodedTrackBean);
			}
			encodedTrackDao.remove(encodedTrackBean);
			log.info(
					"Removed " + encodedTrackBean.getEncoderBean().getExtension() +
					" conversion of " + encodedTrackBean.getFlacUrl() + " " + encodedTrackBean.getId());
		}
		encodedTrackDao.flush();
		getEncodedService().removeEmptyAlbumsAndArtists();
		return extraTracks.size();
	}

	@Override
	public void updateMissingAlbumInformation() {
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		FlacTrackDao flacTrackDao = getFlacTrackDao();
		FlacService flacService = getFlacService();
		for (EncodedTrackBean encodedTrackBean : encodedTrackDao.findTracksWithoutAnAlbum()) {
			String flacUrl = encodedTrackBean.getFlacUrl();
			FlacAlbumBean flacAlbumBean = flacTrackDao.findByUrl(flacUrl).getFlacAlbumBean();
			EncodedAlbumBean encodedAlbumBean = flacService.findOrCreateEncodedAlbumBean(flacAlbumBean);
			encodedTrackBean.setEncodedAlbumBean(encodedAlbumBean);
			encodedTrackDao.store(encodedTrackBean);
			log.info("Updated album information for " + flacUrl);
		}
	}
	
	@Override
	public void updateOwnership() {
		final Map<String, SortedSet<EncodedAlbumBean>> albumsByOwner = new HashMap<String, SortedSet<EncodedAlbumBean>>();
		final Map<String, SortedSet<EncodedArtistBean>> artistsByOwner = new HashMap<String, SortedSet<EncodedArtistBean>>();
		final FlacService flacService = getFlacService();
		String rootDirectory = StringUtils.removeStart(getFlacService().getRootUrl(), "file://");
		final int rootDepth = StringUtils.split(rootDirectory, "/").length;
		final Pattern pattern = Pattern.compile("owner\\.(.+)");
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				String path = file.getAbsolutePath();
				String name = FilenameUtils.getName(path);
				String dir = FilenameUtils.getFullPath(path);
				Matcher matcher = pattern.matcher(name);
				if (matcher.matches()) {
					String owner = matcher.group(1);
					int depth = StringUtils.split(dir, "/").length - rootDepth;
					FlacAlbumBean flacAlbumBean = getFlacService().findFlacAlbumByPath(dir);
					if (flacAlbumBean != null) {
						EncodedAlbumBean encodedAlbumBean = flacService.findOrCreateEncodedAlbumBean(flacAlbumBean);
						if (depth == 1) {
							add(owner, artistsByOwner, encodedAlbumBean.getEncodedArtistBean(), new TreeSet<EncodedArtistBean>());
						}
						else if (depth == 2) {
							add(owner, albumsByOwner, encodedAlbumBean, new TreeSet<EncodedAlbumBean>());
						}
					}
				}
				return false; 
			}
			
			protected <E extends Comparable<E>> void add(
					String owner, Map<String, SortedSet<E>> map, E element, SortedSet<E> empty) {
				SortedSet<E> existing = map.get(owner);
				if (existing == null) {
					map.put(owner, empty);
					existing = empty;
				}
				existing.add(element);
			}
		};
		FileUtils.listFiles(new File(rootDirectory), new DelegateFileFilter(filter), TrueFileFilter.INSTANCE);
		SortedSet<String> owners = new TreeSet<String>(albumsByOwner.keySet());
		owners.addAll(artistsByOwner.keySet());
		for (String owner : owners) {
			getOwnerService().updateOwnership(owner, artistsByOwner.get(owner), albumsByOwner.get(owner));
		}
	}

	@Override
	public void populateCommandCache(SortedMap<String, File> commandCache) throws IOException {
		SortedSet<EncoderBean> allEncoderBeans = getEncoderDao().getAll();
		for (EncoderBean encoderBean : allEncoderBeans) {
			commandCache.put(encoderBean.getExtension(), createCommandFile(encoderBean));
		}
	}
	
	@Override
	public void offerAll(BlockingQueue<EncodingCommandBean> encodingCommandBeans) {
		for (FlacTrackBean flacTrackBean : getFlacTrackDao().getAll()) {
			for (EncoderBean encoderBean : getEncoderDao().getAll()) {
				encodingCommandBeans.offer(new EncodingCommandBean(encoderBean.getExtension(), flacTrackBean.getUrl()));
			}
		}
	}
	@Override
	public void updateAllFilenames() {
		getEncodedService().updateAllFilenames();
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

	public SlimServerService getSlimServerService() {
		return i_slimServerService;
	}

	@Required
	public void setSlimServerService(SlimServerService slimServerService) {
		i_slimServerService = slimServerService;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	@Required
	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	@Required
	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	public SingleEncoderService getSingleEncoderService() {
		return i_singleEncoderService;
	}

	@Required
	public void setSingleEncoderService(SingleEncoderService singleEncoderService) {
		i_singleEncoderService = singleEncoderService;
	}

	public FlacService getFlacService() {
		return i_flacService;
	}

	@Required
	public void setFlacService(FlacService flacService) {
		i_flacService = flacService;
	}

	public OwnerService getOwnerService() {
		return i_ownerService;
	}

	@Required
	public void setOwnerService(OwnerService ownerService) {
		i_ownerService = ownerService;
	}
}
