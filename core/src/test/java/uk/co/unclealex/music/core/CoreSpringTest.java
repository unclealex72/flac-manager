package uk.co.unclealex.music.core;

import java.io.InputStream;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.core.dao.DeviceDao;
import uk.co.unclealex.music.core.dao.EncodedAlbumDao;
import uk.co.unclealex.music.core.dao.EncodedArtistDao;
import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.dao.EncoderDao;
import uk.co.unclealex.music.core.dao.OwnerDao;
import uk.co.unclealex.music.core.initialise.Initialiser;
import uk.co.unclealex.music.core.initialise.TrackImporter;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.service.EncodedService;

public abstract class CoreSpringTest extends SpringTest {

	private Initialiser i_initialiser;
	private TrackImporter i_trackImporter;
	private EncodedService i_encodedService;
	private EncoderDao i_encoderDao;
	private EncodedTrackDao i_encodedTrackDao;
	private EncodedAlbumDao i_encodedAlbumDao;
	private EncodedArtistDao i_encodedArtistDao;
	private OwnerDao i_ownerDao;
	private DeviceDao i_deviceDao;
	
	protected static final String[] TRACKS = {
		"Napalm Death/Scum/24 - Your Achievement_ (Bonus Track)",
		"Napalm Death/Scum/25 - Dead (Bonus Track)",
		"Napalm Death/From Enslavement To Obliteration/12 - You Suffer",
		"S.O.D./Speak English Or Die/21 - Diamonds And Rust (Extended Version)",
		"S.O.D./Speak English Or Die/20 - The Ballad Of Jimi Hendrix",
		"Brutal Truth/Extreme Conditions Demand Extreme Responses/07 - Collateral Damage",
		"Brutal Truth/Sounds of The Animal Kingdom Kill Trend Suicide/09 - Callous"
	};
	
	@Override
	protected void onSetUpBeforeTransaction() throws Exception {
		Initialiser initialiser = getInitialiser();
		initialiser.clear();
		initialiser.initialise();
		EncodedService encodedService = getEncodedService();
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		
		SortedSet<EncoderBean> encoderBeans = getEncoderDao().getAll();
		
		TrackImporter trackImporter = getTrackImporter();
		Pattern pattern = Pattern.compile("(.+?)/(.+?)/([0-9]+) - (.+)");
		ClassLoader classLoader = getClass().getClassLoader();
		
		long encodingTime = getEncodingTime();
		
		for (String track : TRACKS) {
			Matcher matcher = pattern.matcher(track);
			matcher.matches();
			String artist = matcher.group(1);
			String album = matcher.group(2);
			int trackNumber = new Integer(matcher.group(3));
			String title = matcher.group(4);
			EncodedArtistBean encodedArtistBean = 
				encodedService.findOrCreateArtist(makeIdentifier(artist), artist);
			EncodedAlbumBean encodedAlbumBean = 
				encodedService.findOrCreateAlbum(encodedArtistBean, makeIdentifier(album), album);
			for (EncoderBean encoderBean : encoderBeans) {
				String url = "music/" + track + "." + encoderBean.getExtension();
				InputStream in = classLoader.getResourceAsStream(url);
				EncodedTrackBean encodedTrackBean = 
					trackImporter.importTrack(in, in.available(), encoderBean, title, url, trackNumber, encodingTime, encodedAlbumBean);
				encodedTrackBean.setEncodedAlbumBean(encodedAlbumBean);
				encodedTrackDao.store(encodedTrackBean);
			}
		}
	}
	
	protected long getEncodingTime() {
		return System.currentTimeMillis();
	}

	protected String makeIdentifier(String str) {
		StringBuffer retVal = new StringBuffer();
		for (char ch : str.toUpperCase().toCharArray()) {
			if ((ch >= 'A' && ch <= 'Z') || ch == ' ') {
				retVal.append(ch);
			}
		}
		return retVal.toString();
	}
	
	@Override
	protected void onTearDownAfterTransaction() {
		getInitialiser().clear();
	}

	@Override
	protected String[] getConfigLocations() {
		return new String[] {
			"applicationContext-music-core.xml", "applicationContext-music-core-test.xml", 
			"applicationContext-music-core-flac-hibernate-direct.xml" }; 
	}

	public Initialiser getInitialiser() {
		return i_initialiser;
	}

	@Required
	public void setInitialiser(Initialiser initialiser) {
		i_initialiser = initialiser;
	}

	public TrackImporter getTrackImporter() {
		return i_trackImporter;
	}

	@Required
	public void setTrackImporter(TrackImporter trackImporter) {
		i_trackImporter = trackImporter;
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	@Required
	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
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

	public EncodedAlbumDao getEncodedAlbumDao() {
		return i_encodedAlbumDao;
	}

	@Required
	public void setEncodedAlbumDao(EncodedAlbumDao encodedAlbumDao) {
		i_encodedAlbumDao = encodedAlbumDao;
	}

	public EncodedArtistDao getEncodedArtistDao() {
		return i_encodedArtistDao;
	}

	@Required
	public void setEncodedArtistDao(EncodedArtistDao encodedArtistDao) {
		i_encodedArtistDao = encodedArtistDao;
	}

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}

	@Required
	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}

	public DeviceDao getDeviceDao() {
		return i_deviceDao;
	}

	@Required
	public void setDeviceDao(DeviceDao deviceDao) {
		i_deviceDao = deviceDao;
	}
}
