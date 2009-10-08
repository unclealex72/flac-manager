package uk.co.unclealex.music.encoder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import uk.co.unclealex.music.base.initialise.Initialiser;

public abstract class EncoderSpringTest extends SpringTest {

	private Initialiser i_initialiser;
	private File i_importMusicDirectory;
	private File i_encodedMusicStorageDirectory;

	protected void finalize() throws Exception {
		for (File musicDirectory : getMusicDirectories()) {
			if (musicDirectory.exists()) {
				FileUtils.deleteDirectory(musicDirectory);
			}
		}
	}
	
	protected Collection<File> getMusicDirectories() {
		return Arrays.asList(new File[] { getImportMusicDirectory(), getEncodedMusicStorageDirectory() });
	}

	@Override
	protected String[] getConfigLocations() {
		List<String> configLocations = new ArrayList<String>();
		String[] sharedLocations = new String[] {
			"classpath*:applicationContext-music-encoder.xml",
			"classpath*:applicationContext-music-album-covers.xml",
			"classpath*:applicationContext-music-core.xml",
			"classpath*:applicationContext-music-test.xml",
			"classpath*:applicationContext-music-test-flac.xml"
		};
		String[] extraLocations = getExtraConfigLocations();
		for (String[] locations : new String[][] { sharedLocations, extraLocations }) {
			configLocations.addAll(Arrays.asList(locations));
		}
		return configLocations.toArray(new String[0]);
	}

	protected String[] getExtraConfigLocations() {
		return new String[0];
	}
	public Initialiser getInitialiser() {
		return i_initialiser;
	}

	public void setInitialiser(Initialiser initialiser) {
		i_initialiser = initialiser;
	}

	public File getImportMusicDirectory() {
		return i_importMusicDirectory;
	}

	public void setImportMusicDirectory(File importMusicDirectory) {
		i_importMusicDirectory = importMusicDirectory;
	}

	public File getEncodedMusicStorageDirectory() {
		return i_encodedMusicStorageDirectory;
	}

	public void setEncodedMusicStorageDirectory(File encodedMusicStorageDirectory) {
		i_encodedMusicStorageDirectory = encodedMusicStorageDirectory;
	}

}
