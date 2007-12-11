package uk.co.unclealex.music.service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

import uk.co.unclealex.music.core.service.DeviceServiceImpl;

public class DeviceServiceImplTest extends TestCase {

	public void testFindMusicFiles() throws IOException {
		String[] paths = { "xyz/123/out.mp3", "xyz/123/time.out", "abc/123/ketchup.sauce"};
		File dir = createFiles(paths);
		try {
			DeviceServiceImpl deviceServiceImpl = new DeviceServiceImpl();
			Map<File, Boolean> tests = new LinkedHashMap<File, Boolean>();
			tests.put(new File(dir, "xyz"), true);
			tests.put(new File(dir, "abc"), false);
			for (Map.Entry<File, Boolean> entry : tests.entrySet()) {
				File directory = entry.getKey();
				assertEquals(
						"Testing looking for music files in directory " + directory + " failed.",
						entry.getValue().booleanValue(), 
						deviceServiceImpl.containsMusicFile(directory, "mp3"));			
			}
			deviceServiceImpl.removeMusicFolders("mp3", dir);
			for (Map.Entry<File, Boolean> entry : tests.entrySet()) {
				File directory = entry.getKey();
				assertEquals(
						"Testing for removing music files in directory " + directory + " failed.",
						entry.getValue().booleanValue(), 
						!directory.exists());			
			}
			
		}
		finally {
			FileUtils.deleteDirectory(dir);
		}
	}
	
	protected File createFiles(String[] relativePaths) throws IOException {
		File tempDir = File.createTempFile(getClass().getName(), "dir");
		tempDir.delete();
		tempDir.mkdirs();
		for (String relativePath : relativePaths) {
			File f = new File(tempDir, relativePath);
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		return tempDir;
	}
}
