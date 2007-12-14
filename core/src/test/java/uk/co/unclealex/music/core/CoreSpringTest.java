package uk.co.unclealex.music.core;

import java.io.InputStream;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.core.dao.EncoderDao;
import uk.co.unclealex.music.core.initialise.Initialiser;
import uk.co.unclealex.music.core.initialise.TrackImporter;
import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.core.service.EncodedService;

public class CoreSpringTest extends SpringTest {

	private Initialiser i_initialiser;
	private TrackImporter i_trackImporter;
	private EncodedService i_encodedService;
	private EncoderDao i_encoderDao;
	
	protected static final String[] TRACKS = {
		"Napalm Death/Scum/24 - Your Achievement_ (Bonus Track)",
		"Napalm Death/Scum/25 - Dead (Bonus Track)",
		"Napalm Death/From Enslavement To Obliteration/12 - You Suffer",
		"S.O.D./Speak English Or Die/21 - Diamonds And Rust (Extended Version)",
		"S.O.D./Speak English Or Die/22 - Identity",
		"Brutal Truth/Extreme Conditions Demand Extreme Responses/07 - Collateral Damage",
		"Brutal Truth/Sounds of The Animal Kingdom Kill Trend Suicide/09 - Callous"
	};
	
	@Override
	protected void onSetUpBeforeTransaction() throws Exception {
		super.onSetUpBeforeTransaction();
		getInitialiser().initialise();
		EncodedService encodedService = getEncodedService();
		SortedSet<EncoderBean> encoderBeans = getEncoderDao().getAll();
		
		TrackImporter trackImporter = getTrackImporter();
		Pattern pattern = Pattern.compile(".+?/.+?/[0-9]+ - .+");
		ClassLoader classLoader = getClass().getClassLoader();
		
		for (String track : TRACKS) {
			Matcher matcher = pattern.matcher(track);
			String artist = matcher.group(1);
			String album = matcher.group(2);
			int trackNumber = new Integer(matcher.group(3));
			String title = matcher.group(4);
			EncodedArtistBean encodedArtistBean = encodedService.findOrCreateArtist(artist, artist);
			EncodedAlbumBean encodedAlbumBean = encodedService.findOrCreateAlbum(encodedArtistBean, album, album);
			for (EncoderBean encoderBean : encoderBeans) {
				String url = "music/" + track + "." + encoderBean.getExtension();
				InputStream in = classLoader.getResourceAsStream(url);
				trackImporter.importTrack(in, encoderBean, encodedAlbumBean, title, url, trackNumber, 0);
			}
		}
	}
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { "applicationContext-music-core.xml", "applicationContext-music-core.xml" }; 
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

}
