package uk.co.unclealex.music.encoder;

import uk.co.unclealex.music.core.initialise.Initialiser;

public abstract class EncoderSpringTest extends SpringTest {

	private Initialiser i_initialiser;
	
	@Override
	protected void onSetUpBeforeTransaction() throws Exception {
		super.onSetUpBeforeTransaction();
		getInitialiser().initialise();
	}
	
	@Override
	protected void onTearDownAfterTransaction() throws Exception {
		getInitialiser().clear();
		super.onTearDownAfterTransaction();
	}
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] {
			"classpath*:applicationContext-music-encoder-encoder-test.xml",
			"classpath*:applicationContext-music-album-covers.xml",
			"classpath*:applicationContext-music-core.xml",
			"classpath*:applicationContext-music-core-test.xml",
			"classpath*:applicationContext-music-encoder-flac-test.xml"
		};
	}

	public Initialiser getInitialiser() {
		return i_initialiser;
	}

	public void setInitialiser(Initialiser initialiser) {
		i_initialiser = initialiser;
	}

}
