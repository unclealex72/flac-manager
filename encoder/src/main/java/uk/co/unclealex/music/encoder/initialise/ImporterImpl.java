package uk.co.unclealex.music.encoder.initialise;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jcr.RepositoryException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.initialise.TrackImporter;
import uk.co.unclealex.music.base.io.DataExtractor;
import uk.co.unclealex.music.base.io.InputStreamCopier;
import uk.co.unclealex.music.base.io.KnownLengthOutputStream;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.filesystem.ClearableRepositoryFactory;
import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;
import uk.co.unclealex.music.encoder.service.SingleEncoderService;

@Service
public class ImporterImpl implements Importer {

	private static final Logger log = Logger.getLogger(ImporterImpl.class);
	
	private EncoderDao i_encoderDao;
	private EncodedTrackDao i_encodedTrackDao;
	private SingleEncoderService i_singleEncoderService;
	private FlacTrackDao i_flacTrackDao;
	private TrackImporter i_trackImporter;
	private DataExtractor<EncodedTrackBean> i_encodedTrackDataExtractor;
	private InputStreamCopier<EncodedTrackBean> i_inputStreamCopier;
	private RepositoryManager i_encodedRepositoryManager;
	private ClearableRepositoryFactory i_encodedRepositoryFactory;
	
	public void importTracks() throws IOException, RepositoryException {
		final TrackImporter trackImporter = getTrackImporter();
		log.info("Importing tracks.");
		File baseDir = new File("/home/converted");
		final EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		final List<String> extensions = new ArrayList<String>();
		final Map<String, EncoderBean> encodedBeansByExtension = new HashMap<String, EncoderBean>();
		for (EncoderBean encoderBean : getEncoderDao().getAll()) {
			String extension = encoderBean.getExtension();
			extensions.add(extension);
			encodedBeansByExtension.put(extension, encoderBean);
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches("[0-9]+\\.(" + StringUtils.join(extensions, '|') +")");
			}
		};
		File[] files = baseDir.listFiles(filter);
		Comparator<File> comparator = new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				String name1 = f1.getName();
				String name2 = f2.getName();
				int id1 = Integer.parseInt(FilenameUtils.getBaseName(name1));
				int id2 = Integer.parseInt(FilenameUtils.getBaseName(name2));
				return id1==id2?f1.compareTo(f2):id1-id2;
			}
		};
		SortedSet<File> sortedFiles = new TreeSet<File>(comparator);
		sortedFiles.addAll(Arrays.asList(files));
		for (File file : sortedFiles) {
			String filename = file.getName();
			int flacTrackId = Integer.parseInt(FilenameUtils.getBaseName(filename));
			FlacTrackBean flacTrackBean = getFlacTrackDao().findById(flacTrackId);
			if (flacTrackBean != null) {
				String url = flacTrackBean.getUrl();
				String extension = FilenameUtils.getExtension(filename);
				EncoderBean encoderBean = encodedBeansByExtension.get(extension);
				EncodedTrackBean encodedTrackBean =
					encodedTrackDao.findByUrlAndEncoderBean(url, encoderBean);
				if (encodedTrackBean == null) {
					InputStream in = new FileInputStream(file);
					trackImporter.importTrack(
						in, (int) file.length(), encoderBean, flacTrackBean.getTitle(), flacTrackBean.getUrl(), 
						flacTrackBean.getTrackNumber(), file.lastModified(), null);
					IOUtils.closeQuietly(in);
					FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
					log.info(
							"Imported " + flacTrackBean.getTitle() + ", " + flacAlbumBean.getTitle() + 
							" by " + flacAlbumBean.getFlacArtistBean().getName());
				}
			}
		}
		getSingleEncoderService().updateMissingAlbumInformation();
		getEncodedRepositoryFactory().clearNextInstance();
		getEncodedRepositoryManager().updateFromScratch();
	}
	
	@Override
	@Transactional
	public void exportTracks() throws IOException {
		FlacTrackDao flacTrackDao = getFlacTrackDao();
		DataExtractor<EncodedTrackBean> encodedTrackDataExtractor = getEncodedTrackDataExtractor();
		for (EncodedTrackBean encodedTrackBean : getEncodedTrackDao().getAll()) {
			String flacUrl = encodedTrackBean.getFlacUrl();
			String extension = encodedTrackBean.getEncoderBean().getExtension();
			FlacTrackBean flacTrackBean = flacTrackDao.findByUrl(flacUrl);
			if (flacTrackBean == null) {
				log.info("Ignoring " + extension + " version of " + flacUrl + " as it is not a valid flac track.");
			}
			else {
				String filename = flacTrackBean.getId() + "." + encodedTrackBean.getEncoderBean().getExtension();
				File file = new File(new File("/home/converted"), filename);
				if (file.exists()) {
					log.info("Ignoring " + encodedTrackBean + " as it has already been exported.");
				}
				else {
					log.info("Exporting " + encodedTrackBean + " to " + file);
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					KnownLengthOutputStream knownLengthOutputStream = 
						new KnownLengthOutputStream(fileOutputStream, fileOutputStream.getChannel()) {
						@Override
						public void setLength(int length) throws IOException {
							// Do nothing
						}
					};
					getInputStreamCopier().copy(encodedTrackDataExtractor, encodedTrackBean, knownLengthOutputStream);
					fileOutputStream.close();
				}
			}
		}
	}
	
	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	@Required
	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	public TrackImporter getTrackImporter() {
		return i_trackImporter;
	}

	@Required
	public void setTrackImporter(TrackImporter trackImporter) {
		i_trackImporter = trackImporter;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	@Required
	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	@Required
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public SingleEncoderService getSingleEncoderService() {
		return i_singleEncoderService;
	}

	@Required
	public void setSingleEncoderService(SingleEncoderService singleEncoderService) {
		i_singleEncoderService = singleEncoderService;
	}

	public DataExtractor<EncodedTrackBean> getEncodedTrackDataExtractor() {
		return i_encodedTrackDataExtractor;
	}

	@Required
	public void setEncodedTrackDataExtractor(DataExtractor<EncodedTrackBean> encodedTrackDataExtractor) {
		i_encodedTrackDataExtractor = encodedTrackDataExtractor;
	}

	public InputStreamCopier<EncodedTrackBean> getInputStreamCopier() {
		return i_inputStreamCopier;
	}

	@Required
	public void setInputStreamCopier(InputStreamCopier<EncodedTrackBean> inputStreamCopier) {
		i_inputStreamCopier = inputStreamCopier;
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
}
