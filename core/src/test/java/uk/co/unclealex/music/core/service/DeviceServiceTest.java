package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import uk.co.unclealex.music.base.dao.OwnerDao;
import uk.co.unclealex.music.base.model.DeviceBean;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.service.DeviceService;
import uk.co.unclealex.music.base.service.DeviceWriter;
import uk.co.unclealex.music.base.writer.TrackWritingException;
import uk.co.unclealex.music.core.CoreSpringTest;

public class DeviceServiceTest extends CoreSpringTest {

	private static final Map<String,String[]> OWNED_ARTISTS;
	private static final Map<String,String[]> EXPECTED_SONGS;
	
	private static final String COLLATERAL_DAMAGE = 
		"B/Brutal Truth/Extreme Conditions Demand Extreme Responses/07 - Collateral Damage";
	private static final String CALLOUS = 
		"B/Brutal Truth/Sounds of The Animal Kingdom Kill Trend Suicide/09 - Callous";
	private static final String YOUR_ACHIEVEMENT = "N/Napalm Death/Scum/24 - Your Achievement_ (Bonus Track)";
	private static final String DEAD = "N/Napalm Death/Scum/25 - Dead (Bonus Track)";
	private static final String YOU_SUFFER = "N/Napalm Death/From Enslavement To Obliteration/12 - You Suffer";
	private static final String JIMI_HENDRIX = "S/S.O.D/Speak English Or Die/20 - The Ballad Of Jimi Hendrix";
	private static final String DIAMONDS_AND_RUST = "S/S.O.D/Speak English Or Die/21 - Diamonds And Rust (Extended Version)";
	private static final String BOHEMIAN_RHAPSODY = "Q/Queen/A Night At The Opera/09 - Bohemian Rhapsody";
	private static final String DATA_FILE_A = "data/data_a.txt";
	private static final String DATA_FILE_B = "data/data_b.txt";

	static {
		OWNED_ARTISTS = new HashMap<String, String[]>();
		OWNED_ARTISTS.put("Alex", new String[] { "NAPALM DEATH", "BRUTAL TRUTH" });
		OWNED_ARTISTS.put("Trevor", new String[] { "NAPALM DEATH", "SOD" });
		
		EXPECTED_SONGS = new HashMap<String, String[]>();
		EXPECTED_SONGS.put(
			"mp3", 
			new String[] { COLLATERAL_DAMAGE, CALLOUS, YOUR_ACHIEVEMENT, DEAD, YOU_SUFFER, DATA_FILE_A, DATA_FILE_B });
		EXPECTED_SONGS.put(
			"ogg", 
			new String[] { YOUR_ACHIEVEMENT, DEAD, YOU_SUFFER, JIMI_HENDRIX, DIAMONDS_AND_RUST, DATA_FILE_A, DATA_FILE_B });
	}
	
	private DeviceService i_deviceService;
	private DeviceWriter i_deviceWriter;
	
	@Override
	protected long getEncodingTime() {
		// Encode an hour in the past so that there are no problems with the granularity of
		// file modification times cannot cause problems.
		return System.currentTimeMillis() - (1000 * 3600);
	}
	
	@Override
	protected void onSetUpInTransaction() {
		OwnerDao ownerDao = getOwnerDao();
		for (OwnerBean ownerBean : ownerDao.getAll()) {
			ownerBean.setOwnsAll(false);
			ownerBean.setEncodedAlbumBeans(new TreeSet<EncodedAlbumBean>());
			SortedSet<EncodedArtistBean> encodedArtistBeans = new TreeSet<EncodedArtistBean>();
			for (String artistIdentifier : OWNED_ARTISTS.get(ownerBean.getName())) {
				EncodedArtistBean encodedArtistBean = getEncodedArtistDao().findByIdentifier(artistIdentifier);
				SortedSet<OwnerBean> ownerBeans = new TreeSet<OwnerBean>();
				ownerBeans.add(ownerBean);
				encodedArtistBean.setOwnerBeans(ownerBeans);
				encodedArtistBeans.add(encodedArtistBean);
			}
			ownerBean.setEncodedArtistBeans(encodedArtistBeans);
			ownerDao.store(ownerBean);
		}
	}
	
	public void testWriteToAll() throws IOException, TrackWritingException {
		DeviceService deviceService = getDeviceService();
		DeviceWriter deviceWriter = getDeviceWriter();
		SortedMap<DeviceBean, String> pathsByDeviceBean = deviceService.findDevicesAndFiles();
		try {
			for (Map.Entry<DeviceBean, String> entry : pathsByDeviceBean.entrySet()) {
				File root = new File(entry.getValue());
				createFile(YOU_SUFFER, root, entry.getKey());
				createFile(BOHEMIAN_RHAPSODY, root, entry.getKey());
				createFile(DATA_FILE_A, root, entry.getKey());
				createFile(DATA_FILE_B, root, entry.getKey());
			}		
			deviceWriter.writeToAllDevices();
			for (Map.Entry<DeviceBean, String> entry : pathsByDeviceBean.entrySet()) {
				DeviceBean deviceBean = entry.getKey();
				File path = new File(entry.getValue());
				checkCorrectFiles(deviceBean, path);
				checkEmpty(deviceBean, path, YOUR_ACHIEVEMENT, false);
				checkEmpty(deviceBean, path, YOU_SUFFER, !deviceBean.isDeletingRequired());
			}
		}
		finally {
			for (String path : pathsByDeviceBean.values()) {
				File f = new File(path);
				if (f.exists()) {
					FileUtils.deleteDirectory(f);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void checkCorrectFiles(DeviceBean deviceBean, File path) {
		IOFileFilter filter = new IOFileFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return true;
			}
			@Override
			public boolean accept(File file) {
				return true;
			}
		};
		SortedSet<String> actualSongs = new TreeSet<String>();
		Collection<File> foundFiles = (Collection<File>) FileUtils.listFiles(path, filter, filter);
		for (File f : foundFiles) {
			String songName = f.getPath().substring(path.getPath().length());
			if (songName.startsWith(File.separator)) {
				songName = songName.substring(1);
				actualSongs.add(songName);
			}
		}
		String ext = deviceBean.getEncoderBean().getExtension();
		SortedSet<String> expectedSongs = new TreeSet<String>();
		String[] songs = EXPECTED_SONGS.get(ext);
		for (String song : songs) {
			expectedSongs.add(getSongName(song, ext));
		}
		assertEquals(
			"The songs written for the " + ext + " device were incorrect.",
			expectedSongs, actualSongs);
	}

	private void checkEmpty(DeviceBean deviceBean, File path, String songName, boolean shouldBeEmpty) {
		File song = getFile(songName, path, deviceBean);
		if (shouldBeEmpty) {
			assertEquals("The song " + song + " should be empty.", 0, song.length());
		}
		else {
			assertFalse("The song " + song + " should not be empty.", song.length() == 0);
		}
	}

	protected File getFile(String songName, File root, DeviceBean deviceBean) {
		return new File(root, getSongName(songName, deviceBean.getEncoderBean().getExtension()));
	}
	
	protected String getSongName(String songName, String ext) {
		if ("".equals(FilenameUtils.getExtension(songName))) {
			songName += "." + ext;
		}
		return songName;
	}
	protected void createFile(String songName, File root, DeviceBean deviceBean) throws IOException {
		File f = getFile(songName, root, deviceBean);
		f.getParentFile().mkdirs();
		if (f.exists()) {
			f.delete();
		}
		f.createNewFile();
	}

	public DeviceService getDeviceService() {
		return i_deviceService;
	}
	
	public void setDeviceService(DeviceService deviceService) {
		i_deviceService = deviceService;
	}

	public DeviceWriter getDeviceWriter() {
		return i_deviceWriter;
	}

	public void setDeviceWriter(DeviceWriter deviceWriter) {
		i_deviceWriter = deviceWriter;
	}
}
