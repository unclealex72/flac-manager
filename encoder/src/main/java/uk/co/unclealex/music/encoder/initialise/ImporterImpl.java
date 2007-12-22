package uk.co.unclealex.music.encoder.initialise;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.dao.EncoderDao;
import uk.co.unclealex.music.core.initialise.TrackImporter;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.encoder.dao.FlacTrackDao;
import uk.co.unclealex.music.encoder.model.FlacTrackBean;
import uk.co.unclealex.music.encoder.service.EncoderService;

@Service
@Transactional
public class ImporterImpl implements Importer {

	private static final Logger log = Logger.getLogger(ImporterImpl.class);
	
	private static final String FLAC_FILE_BASE = "/mnt/multimedia/flac/";
	private static final String FLAC_URL_BASE = "file://" + FLAC_FILE_BASE;

	private EncoderDao i_encoderDao;
	private EncodedTrackDao i_encodedTrackDao;
	private EncoderService i_encoderService;
	private FlacTrackDao i_flacTrackDao;
	private TrackImporter i_trackImporter;
	
	public void importTracks() throws IOException {
		TrackImporter trackImporter = getTrackImporter();
		log.info("Importing tracks.");
		File baseDir = new File("/home/converted");
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		for (EncoderBean encoderBean : getEncoderDao().getAll()) {
			SortedMap<File, FlacTrackBean> tracksByFile = new TreeMap<File, FlacTrackBean>();
			File base = new File(new File(baseDir, encoderBean.getExtension()), "raw");
			collect(base, tracksByFile, encoderBean.getExtension());
			for (Map.Entry<File, FlacTrackBean> entry : tracksByFile.entrySet()) {
				File file = entry.getKey();
				FlacTrackBean flacTrackBean = entry.getValue();
				if (flacTrackBean != null) {
					String url = flacTrackBean.getUrl();
					EncodedTrackBean encodedTrackBean =
						encodedTrackDao.findByUrlAndEncoderBean(url, encoderBean);
					if (encodedTrackBean == null) {
						InputStream in = new FileInputStream(file);
						trackImporter.importTrack(
							in, encoderBean, flacTrackBean.getTitle(), flacTrackBean.getUrl(), flacTrackBean.getTrackNumber(), 
							file.lastModified(), null);
						in.close();
					}
				}
			}
		}
		getEncoderService().updateMissingAlbumInformation();
	}
	
	protected void collect(File base, SortedMap<File, FlacTrackBean> tracksByFile, final String ext) throws IOException {
		File[] files = base.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory() || file.getName().endsWith("." + ext);
				}
			});
		for (File file : files) {
			if (file.isDirectory()) {
				collect(file, tracksByFile, ext);
			}
			else {
				tracksByFile.put(file, extractFlacTrackBean(file, ext));
			}
		}
	}

	
	protected FlacTrackBean extractFlacTrackBean(File file, String ext) throws IOException {
		List<String> parts = new ArrayList<String>(Arrays.asList(StringUtils.split(file.getCanonicalPath(), '/')));
		Collections.reverse(parts);
		String trackAndTitle = parts.get(0).toLowerCase();
		trackAndTitle = trackAndTitle.substring(0, trackAndTitle.lastIndexOf('.'));
		String album = sanitise(parts.get(1));
		String artist = sanitise(parts.get(2)).toLowerCase();
		String[] trackAndTitleArray = StringUtils.split(trackAndTitle, "-", 2);
		String track = trackAndTitleArray[0].trim();
		String title = sanitise(trackAndTitleArray[1].trim());
		String url = FLAC_URL_BASE + artist + "/" + album + "/" + track + "_" + title + ".flac";
		FlacTrackBean flacTrackBean = getFlacTrackDao().findByUrl(url);

		if (flacTrackBean == null) {
			url = FLAC_URL_BASE + "the_" + artist + "/" + album + "/" + track + "_" + title + ".flac";
			flacTrackBean = getFlacTrackDao().findByUrl(url);
		}
		return flacTrackBean;
	}

	protected String sanitise(String str) {
		StringBuffer buff = new StringBuffer();
		for (char c : str.toCharArray()) {
			if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
				buff.append(c);
			}
		}
		return buff.toString().toLowerCase().replace(' ', '_');
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
}
