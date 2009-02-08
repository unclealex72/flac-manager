package uk.co.unclealex.music.encoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.unclealex.music.core.initialise.Initialiser;

public abstract class EncoderSpringTest extends SpringTest {

	private Initialiser i_initialiser;
	
	@Override
	protected void onSetUpBeforeTransaction() throws Exception {
		super.onSetUpBeforeTransaction();
		Initialiser initialiser = getInitialiser();
		initialiser.clear();
		initialiser.initialise();
	}
	
	@Override
	protected String[] getConfigLocations() {
		List<String> configLocations = new ArrayList<String>();
		String[] sharedLocations = new String[] {
			"classpath*:applicationContext-music-encoder-encoder-test.xml",
			"classpath*:applicationContext-music-album-covers.xml",
			"classpath*:applicationContext-music-core.xml",
			"classpath*:applicationContext-music-core-test.xml"
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

}
