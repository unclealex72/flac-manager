package uk.co.unclealex.music.encoder.initialise;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FilenameUtils;
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
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;
import uk.co.unclealex.music.core.service.CommandWorker;
import uk.co.unclealex.music.encoder.service.EncoderService;

@Service
public class ImporterImpl implements Importer {

	private static final Logger log = Logger.getLogger(ImporterImpl.class);
	
	private EncoderDao i_encoderDao;
	private EncodedTrackDao i_encodedTrackDao;
	private EncoderService i_encoderService;
	private FlacTrackDao i_flacTrackDao;
	private TrackImporter i_trackImporter;
	private DataExtractor<EncodedTrackBean> i_encodedTrackDataExtractor;
	private InputStreamCopier<EncodedTrackBean> i_inputStreamCopier;
	private RepositoryManager i_encodedRepositoryManager;
	
	public void importTracks() throws IOException {
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
		
		final SortedMap<ImportCommandBean, Throwable> errors =
			Collections.synchronizedSortedMap(new TreeMap<ImportCommandBean, Throwable>());
		final BlockingQueue<ImportCommandBean> importCommandBeans = new LinkedBlockingQueue<ImportCommandBean>();
		int maximumThreads = 1;
		final ProcessTracker processTracker = new ProcessTracker(sortedFiles.size());
		List<CommandWorker<ImportCommandBean>> workers = new ArrayList<CommandWorker<ImportCommandBean>>(maximumThreads);
		for (int idx = 0; idx < maximumThreads; idx++) {
			CommandWorker<ImportCommandBean> worker = new CommandWorker<ImportCommandBean>(importCommandBeans, errors) {
				@Override
				protected void process(ImportCommandBean importCommandBean) throws IOException {
					File file = importCommandBean.getFile();
					String filename = file.getName();
					int flacTrackId = Integer.parseInt(FilenameUtils.getBaseName(filename));
					FlacTrackBean flacTrackBean = getFlacTrackDao().findById(flacTrackId);
					if (flacTrackBean == null) {
						processTracker.ignore(file);
					}
					else {
						String url = flacTrackBean.getUrl();
						String extension = FilenameUtils.getExtension(filename);
						EncoderBean encoderBean = encodedBeansByExtension.get(extension);
						EncodedTrackBean encodedTrackBean =
							encodedTrackDao.findByUrlAndEncoderBean(url, encoderBean);
						if (encodedTrackBean != null) {
							processTracker.ignore(file);
						}
						else {
							Date start = new Date();
							InputStream in = new FileInputStream(file);
							EncodedTrackBean newEncodedTrackBean = trackImporter.importTrack(
								in, (int) file.length(), encoderBean, flacTrackBean.getTitle(), flacTrackBean.getUrl(), 
								flacTrackBean.getTrackNumber(), file.lastModified(), null);
							in.close();
							processTracker.imported(file, newEncodedTrackBean.toString(), start, new Date());
						}
					}
				}
			};
			worker.start();
			workers.add(worker);
		}
		for (File file : sortedFiles) {
			importCommandBeans.offer(new ImportCommandBean(file));
		}
		for (int idx = 0; idx < maximumThreads; idx++) {
			importCommandBeans.offer(new ImportCommandBean());
		}
		for (CommandWorker<ImportCommandBean> worker : workers) {
			try {
				worker.join();
			}
			catch (InterruptedException e) {
				// Do nothing
			}
		}
		getEncoderService().updateMissingAlbumInformation();
		getEncodedRepositoryManager().refresh();
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
					OutputStream fileOutputStream = new FileOutputStream(file);
					getInputStreamCopier().copy(encodedTrackDataExtractor, encodedTrackBean, fileOutputStream);
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

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	@Required
	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
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
}
