package uk.co.unclealex.flacconverter.encoded.service;

import java.io.IOException;
import java.util.Arrays;

import uk.co.unclealex.flacconverter.encoded.EncodedSpringTest;
import uk.co.unclealex.flacconverter.encoded.dao.EncoderDao;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;

public class FileSystemServiceTest extends EncodedSpringTest {

	private static final String[] PATHS = {
		"B",
		"B/Brutal Truth",
		"B/Brutal Truth/Extreme Conditions Demand Extreme Responses",
		"B/Brutal Truth/Extreme Conditions Demand Extreme Responses/07 - Collateral Damage.",
		"B/Brutal Truth/Sounds Of The Animal Kingdomkill Trend Suicide",
		"B/Brutal Truth/Sounds Of The Animal Kingdomkill Trend Suicide/09 - Callous.",
		"N",
		"N/Napalm Death",
		"N/Napalm Death/From Enslavement To Obliteration",
		"N/Napalm Death/From Enslavement To Obliteration/12 - You Suffer.",
		"N/Napalm Death/Scum",
		"N/Napalm Death/Scum/24 - Your Achievement Bonus Track.",
		"N/Napalm Death/Scum/25 - Dead Bonus Track.",
		"S",
		"S/Sod",
		"S/Sod/Speak English Or Die",
		"S/Sod/Speak English Or Die/20 - The Ballad Of Jimi Hendrix.",
		"S/Sod/Speak English Or Die/21 - Diamonds And Rust Extended Version."
	};
	
	private FileSystemService i_fileSystemService;
	private EncoderService i_encoderService;
	private EncoderDao i_encoderDao;
	
	public void testExistence() throws AlreadyEncodingException, CurrentlyScanningException, IOException {
		FileSystemService fileSystemService = getFileSystemService();
		try {
			getEncoderService().encodeAll();
		}
		catch (MultipleEncodingException e) {
			// No problem for this test.
		}
		for (EncoderBean encoderBean : getEncoderDao().getAll()) {
			String extension = encoderBean.getExtension();
			for (String path : PATHS) {
				boolean isDirectory;
				if (path.endsWith(".")) {
					isDirectory = false;
					path += extension;
				}
				else {
					isDirectory = true;
				}
				for (String prefix : new String[] { "", "/" }) {
					String fullPath = prefix + extension + "/" + path;
					assertTrue("Could not find the object " + fullPath, fileSystemService.exists(fullPath));
					if (isDirectory) {
						assertTrue(fullPath + " should be a directory.", fileSystemService.isDirectory(fullPath));
						assertTrue(fullPath + "/ should be a directory.", fileSystemService.isDirectory(fullPath + "/"));
					}
					else {
						assertTrue(fullPath + " should exist.", fileSystemService.exists(fullPath));
						assertFalse(fullPath + "/ not should exist.", fileSystemService.exists(fullPath + "/"));
					}
				}
			}
		}
		assertEquals(
				"The wrong Napalm Death albums were returned",
				Arrays.asList(new String[] {"From Enslavement To Obliteration", "Scum"}),
				Arrays.asList(fileSystemService.getChildPaths("/mp3/N/Napalm Death")));

		assertEquals(																				
				"The wrong encoded bean was returned",
				"file:///mnt/multimedia/flac/napalm_death/scum/25_dead_bonus_track.flac",
				fileSystemService.getEncodedTrackBean("mp3/N/Napalm Death/Scum/25 - Dead Bonus Track.mp3").getFlacUrl());
}	
	
	public FileSystemServiceImpl getFileSystemServiceImpl() {
		return (FileSystemServiceImpl) getFileSystemService();
	}
	public FileSystemService getFileSystemService() {
		return i_fileSystemService;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		i_fileSystemService = fileSystemService;
	}

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}
}
