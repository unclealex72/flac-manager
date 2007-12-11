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

import uk.co.unclealex.music.core.dao.OwnerDao;
import uk.co.unclealex.music.core.model.DeviceBean;
import uk.co.unclealex.music.core.model.OwnerBean;
import uk.co.unclealex.music.core.service.DeviceService;
import uk.co.unclealex.music.core.writer.TrackWritingException;

public class DeviceServiceTest extends EncodedSpringTest {

	private static final Map<String,String[]> OWNED_ARTISTS;
	private static final Map<String,String[]> EXPECTED_SONGS;
	
	private static final String COLLATERAL_DAMAGE = 
		"B/Brutal Truth/Extreme Conditions Demand Extreme Responses/07 - Collateral Damage";
	private static final String CALLOUS = 
		"B/Brutal Truth/Sounds Of The Animal Kingdomkill Trend Suicide/09 - Callous";
	private static final String YOUR_ACHIEVEMENT = "N/Napalm Death/Scum/24 - Your Achievement Bonus Track";
	private static final String DEAD = "N/Napalm Death/Scum/25 - Dead Bonus Track";
	private static final String YOU_SUFFER = "N/Napalm Death/From Enslavement To Obliteration/12 - You Suffer";
	private static final String JIMI_HENDRIX = "S/Sod/Speak English Or Die/20 - The Ballad Of Jimi Hendrix";
	private static final String DIAMONDS_AND_RUST = "S/Sod/Speak English Or Die/21 - Diamonds And Rust Extended Version";
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
	
	private OwnerDao i_ownerDao;
	private DeviceService i_deviceService;
	private EncoderService i_encoderService;
	
	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		OwnerDao ownerDao = getOwnerDao();
		for (OwnerBean ownerBean : ownerDao.getAll()) {
			ownerBean.setOwnsAll(false);
			ownerBean.setOwnedAlbumBeans(new TreeSet<OwnedAlbumBean>());
			SortedSet<OwnedArtistBean> ownedArtistBeans = new TreeSet<OwnedArtistBean>();
			for (String artistName : OWNED_ARTISTS.get(ownerBean.getName())) {
				OwnedArtistBean ownedArtistBean = new OwnedArtistBean();
				ownedArtistBean.setName(artistName);
				ownedArtistBean.setOwnerBean(ownerBean);
				ownedArtistBeans.add(ownedArtistBean);
			}
			ownerBean.setOwnedArtistBeans(ownedArtistBeans);
			ownerDao.store(ownerBean);
		}
	}
	
	public void testWriteToAll() throws IOException, AlreadyEncodingException, CurrentlyScanningException, TrackWritingException {
		DeviceService deviceService = getDeviceService();
		SortedMap<DeviceBean, String> pathsByDeviceBean = deviceService.findDevicesAndFiles();
		try {
			for (Map.Entry<DeviceBean, String> entry : pathsByDeviceBean.entrySet()) {
				File root = new File(entry.getValue());
				if (root.exists()) {
					FileUtils.deleteDirectory(root);
				}
				createFile(YOUR_ACHIEVEMENT, root, entry.getKey());
				createFile(DATA_FILE_A, root, entry.getKey());
			}
			try {
				getEncoderService().encodeAll();
			}
			catch (MultipleEncodingException e) {
				// Perfectly allowable in this test.
			}
			for (Map.Entry<DeviceBean, String> entry : pathsByDeviceBean.entrySet()) {
				File root = new File(entry.getValue());
				createFile(YOU_SUFFER, root, entry.getKey());
				createFile(BOHEMIAN_RHAPSODY, root, entry.getKey());
				createFile(DATA_FILE_B, root, entry.getKey());
			}		
			deviceService.writeToAllDevices();
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

	public OwnerDao getOwnerDao() {
		return i_ownerDao;
	}
	
	public void setOwnerDao(OwnerDao ownerDao) {
		i_ownerDao = ownerDao;
	}
	
	public DeviceService getDeviceService() {
		return i_deviceService;
	}
	
	public void setDeviceService(DeviceService deviceService) {
		i_deviceService = deviceService;
	}

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}
}
