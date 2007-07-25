package uk.co.unclealex.flacconverter.encoded.initialise;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.dao.DeviceDao;
import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.dao.EncoderDao;
import uk.co.unclealex.flacconverter.encoded.dao.EncodingDao;
import uk.co.unclealex.flacconverter.encoded.dao.OwnerDao;
import uk.co.unclealex.flacconverter.encoded.dao.TrackDataDao;
import uk.co.unclealex.flacconverter.encoded.model.DeviceBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.KeyedBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnedAlbumBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnedArtistBean;
import uk.co.unclealex.flacconverter.encoded.model.OwnerBean;
import uk.co.unclealex.flacconverter.encoded.model.TrackDataBean;
import uk.co.unclealex.flacconverter.encoded.service.AlreadyEncodingException;
import uk.co.unclealex.flacconverter.encoded.service.CurrentlyScanningException;
import uk.co.unclealex.flacconverter.encoded.service.EncoderService;
import uk.co.unclealex.flacconverter.encoded.service.MultipleEncodingException;
import uk.co.unclealex.flacconverter.flac.dao.FlacTrackDao;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class InitialiserImpl implements Initialiser {

	private static final Logger log = Logger.getLogger(InitialiserImpl.class);
	
	private static final String FLAC_FILE_BASE = "/mnt/multimedia/flac/";
	private static final String FLAC_URL_BASE = "file://" + FLAC_FILE_BASE;
	
	public static void main(String[] args) throws IOException, AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-commandline.xml");
		Initialiser initialiser = (Initialiser) applicationContext.getBean("initialiser");
		EncoderService encoderService = (EncoderService) applicationContext.getBean("encoderService");
//		initialiser.initialise();
		initialiser.importTracks();
		encoderService.encodeAll(8);
	}

	private OwnerDao i_ownerDao;
	private EncoderDao i_encoderDao;
	private TrackDataDao i_trackDataDao;
	private DeviceDao i_deviceDao;
	private EncodedTrackDao i_encodedTrackDao;
	private FlacTrackDao i_flacTrackDao;
	
	//@Transactional(propagation=Propagation.NEVER)
	public void importTracks() throws IOException {
		File baseDir = new File("/mnt/multimedia/converted");
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		for (EncoderBean encoderBean : getEncoderDao().getAll()) {
			SortedMap<File, FlacTrackBean> tracksByFile = new TreeMap<File, FlacTrackBean>();
			File base = new File(new File(baseDir, encoderBean.getExtension()), "raw/Z");
			collect(base, tracksByFile, encoderBean.getExtension());
			for (Map.Entry<File, FlacTrackBean> entry : tracksByFile.entrySet()) {
				File file = entry.getKey();
				FlacTrackBean flacTrackBean = entry.getValue();
				if (flacTrackBean != null) {
					String url = flacTrackBean.getUrl();
					EncodedTrackBean encodedTrackBean =
						encodedTrackDao.findByUrlAndEncoderBean(url, encoderBean);
					if (encodedTrackBean == null) {
						importTrack(encoderBean, file, flacTrackBean);
					}
					else {
						TrackDataBean trackDataBean = encodedTrackBean.getTrackDataBean();
						int expectedLength = (int) file.length();
						int actualLength = trackDataBean.getTrack().length;
						if (trackDataBean.getTrack().length != file.length()) {
							Formatter formatter = new Formatter();
							
							log.error(formatter.format("FAILED check %s of %s. Expected %d but found %d.", encoderBean.getExtension(), url, expectedLength, actualLength));
						}
						getTrackDataDao().dismiss(trackDataBean);
						getEncodedTrackDao().dismiss(encodedTrackBean);
					}
				}
			}
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void importTrack(EncoderBean encoderBean, File file, FlacTrackBean flacTrackBean) throws IOException {
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		TrackDataDao trackDataDao = getTrackDataDao();
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int length = IOUtils.copy(in, out);
		in.close();
		out.close();
		TrackDataBean trackDataBean = new TrackDataBean();
		trackDataBean.setTrack(out.toByteArray());
		trackDataBean.setLength(length);
		EncodedTrackBean encodedTrackBean = new EncodedTrackBean();
		encodedTrackBean.setTrackDataBean(trackDataBean);
		encodedTrackBean.setEncoderBean(encoderBean);
		encodedTrackBean.setFlacUrl(flacTrackBean.getUrl());
		encodedTrackBean.setTimestamp(file.lastModified());
		log.info("Storing " + encoderBean.getExtension() + " of " + flacTrackBean.getUrl());
		encodedTrackDao.store(encodedTrackBean);
		trackDataDao.clear();
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
	public void initialise() throws IOException {
		OwnerBean alex = createOwnerBean("Alex", false);
		OwnerBean trevor = createOwnerBean("Trevor", true);
		EncoderBean mp3Encoder = createEncoderBean("mp3", "flac2mp3.sh", "494433");
		EncoderBean oggEncoder = createEncoderBean("ogg", "flac2ogg.sh", "4f676753");
		DeviceBean toughDrive = createDeviceBean("MHV2040AH", "ToughDrive Car HardDrive", alex, mp3Encoder);
		DeviceBean iriver120 = createDeviceBean("MK2004GAL", "iRiver", alex, oggEncoder);
		DeviceBean iriver140 = createDeviceBean("MK2004GAH", "iRiver", trevor, oggEncoder);
		for (OwnerBean ownerBean : new OwnerBean[] { alex, trevor }) {
			getOwnerDao().store(ownerBean);
		}
		for (EncoderBean encoderBean : new EncoderBean[] { mp3Encoder, oggEncoder }) {
			getEncoderDao().store(encoderBean);
		}
		for (DeviceBean deviceBean : new DeviceBean[] { toughDrive, iriver120, iriver140 }) {
			getDeviceDao().store(deviceBean);
		}
	}

	protected OwnerBean createOwnerBean(String name, boolean ownsAll) {
		OwnerBean ownerBean = new OwnerBean();
		ownerBean.setName(name);
		ownerBean.setOwnsAll(ownsAll);
		ownerBean.setDeviceBeans(new TreeSet<DeviceBean>());
		ownerBean.setOwnedAlbumBeans(new TreeSet<OwnedAlbumBean>());
		ownerBean.setOwnedArtistBeans(new TreeSet<OwnedArtistBean>());
		return ownerBean;
	}

	protected EncoderBean createEncoderBean(String extension, String command, String magicNumber) throws IOException {
		EncoderBean encoderBean = new EncoderBean();
		InputStream in = getClass().getResourceAsStream(command);
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer);
		in.close();
		encoderBean.setCommand(writer.toString());
		encoderBean.setExtension(extension);
		encoderBean.setMagicNumber(magicNumber);
		encoderBean.setEncodedTrackBeans(new TreeSet<EncodedTrackBean>());
		encoderBean.setDeviceBeans(new TreeSet<DeviceBean>());
		return encoderBean;
	}
	
	private DeviceBean createDeviceBean(
			String identifier, String description, OwnerBean ownerBean, EncoderBean encoderBean) {
		DeviceBean deviceBean = new DeviceBean();
		deviceBean.setDescription(description);
		deviceBean.setIdentifier(identifier);
		deviceBean.setOwnerBean(ownerBean);
		deviceBean.setEncoderBean(encoderBean);
		deviceBean.setTitleFormat("${1:artist}/${artist}/${album}/${2:track} - ${title}");
		ownerBean.getDeviceBeans().add(deviceBean);
		encoderBean.getDeviceBeans().add(deviceBean);
		return deviceBean;
	}

	public DeviceDao getDeviceDao() {
		return i_deviceDao;
	}

	public void setDeviceDao(DeviceDao deviceDao) {
		i_deviceDao = deviceDao;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

	@SuppressWarnings("unchecked")
	public void clear() {
		for (EncodingDao encodingDao : new EncodingDao[] { getEncoderDao(), getDeviceDao(), getOwnerDao() }) {
			for (Object obj : encodingDao.getAll()) {
				encodingDao.remove((KeyedBean) obj);
			}
		}
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	public TrackDataDao getTrackDataDao() {
		return i_trackDataDao;
	}

	public void setTrackDataDao(TrackDataDao trackDataDao) {
		i_trackDataDao = trackDataDao;
	}


}
