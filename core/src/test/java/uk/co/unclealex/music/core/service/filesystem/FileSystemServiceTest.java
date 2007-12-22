package uk.co.unclealex.music.core.service.filesystem;

import java.util.Arrays;

import uk.co.unclealex.music.core.CoreSpringTest;
import uk.co.unclealex.music.core.model.EncodedTrackBean;

public class FileSystemServiceTest extends CoreSpringTest {

	private FileSystemService i_fileSystemService;
	
	public void testRoot() throws PathNotFoundException {
		testDirectory("", "mp3", "ogg");
	}
	
	public void testEncoder() throws PathNotFoundException {
		testDirectory("mp3", "B", "N", "S");
	}

	public void testFirstLetterOfArtist() throws PathNotFoundException {
		testDirectory("mp3/N", "Napalm Death");
	}

	public void testArtist() throws PathNotFoundException {
		testDirectory("mp3/S/S.O.D.", "Speak English Or Die");
	}

	public void testAlbum() throws PathNotFoundException {
		testDirectory("ogg/N/Napalm Death/Scum", "24 - Your Achievement_ (Bonus Track).ogg", "25 - Dead (Bonus Track).ogg");
	}

	public void testTrack() throws PathNotFoundException {
		FileSystemService fileSystemService = getFileSystemService();
		String path = "/ogg/N/Napalm Death/Scum/24 - Your Achievement_ (Bonus Track).ogg";
		EncodedTrackBean encodedTrackBean =
			fileSystemService.findByPath(path);
		assertEquals("The wrong track title was returned", "Your Achievement_ (Bonus Track)", encodedTrackBean.getTitle());
		assertEquals("The wrong track number was returned", 24, encodedTrackBean.getTrackNumber().intValue());
		assertFalse("The track was identified as a directory", fileSystemService.isDirectory(path));
		path += "/";
		assertFalse("The track followed by a slash was identified as a directory", fileSystemService.isDirectory(path));
		try {
			fileSystemService.findByPath(path);
			fail("The track followed by a slash was identified as a track");
		}
		catch (PathNotFoundException e) {
			// This is what we expect.
		}		
	}
	
	public void testInvalidDirectory() {
		try {
			assertFalse("An invalid directory was identified as a directory", getFileSystemService().isDirectory("/mp3/One"));
		}
		catch (PathNotFoundException e) {
			// This is allowable
		}
	}

	public void testInvalidFile() {
		String[] paths = new String[] {
				"/ogg/N/Napalm Death/Scum/25 - Your Achievement_ (Bonus Track).ogg",
				"/ogg/N/Napalm Death/Scum/24 - Your Achievement_ (Bonus Track).mp3",
				"/ogg/N/Napalm Death/Scum/24 - Your achievement_ (Bonus Track).ogg",
				"/ogg/N/Napalm Death/Scrum/24 - Your Achievement_ (Bonus Track).ogg",
		};
		for (String path : paths) {
			try {
				getFileSystemService().findByPath(path);
				fail(path + " should not be identified as a track.");
			}
			catch (PathNotFoundException e) {
				// Expected
			}
		}
	}
	public void testDirectory(String dir, String... children) throws PathNotFoundException {
		FileSystemService fileSystemService = getFileSystemService();
		for (String path : new String[] { dir, "/" + dir, dir + "/", "/" + dir + "/" }) {
			assertTrue(
					"The directory " + path + " was not recognised as a directory.",
					fileSystemService.isDirectory(path));
			assertEquals("The directory " + path + " had the wrong children.", Arrays
					.asList(children), fileSystemService.getChildren(path));
			try {
				fileSystemService.findByPath(path);
				fail("The directory " + path
						+ " should not be representable as an EncodedTrackBean.");
			}
			catch (PathNotFoundException e) {
				// Expected!!
			}
		}
	}

	public FileSystemService getFileSystemService() {
		return i_fileSystemService;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		i_fileSystemService = fileSystemService;
	}
}
