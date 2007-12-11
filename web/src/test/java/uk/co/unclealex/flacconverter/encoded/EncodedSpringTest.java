package uk.co.unclealex.flacconverter.encoded;

import uk.co.unclealex.music.web.SpringTest;
import uk.co.unclealex.music.web.encoded.initialise.Initialiser;

public abstract class EncodedSpringTest extends SpringTest {

	private Initialiser i_initialiser;
	
	@Override
	protected void onSetUpBeforeTransaction() throws Exception {
		getInitialiser().initialise();
		super.onSetUpBeforeTransaction();
	}
	
	@Override
	protected void onTearDownAfterTransaction() throws Exception {
		getInitialiser().clear();
		super.onTearDownAfterTransaction();
	}
	@Override
	protected String[] getConfigLocations() {
		return new String[] {
			"classpath:applicationContext.xml",
			"classpath:applicationContext-dummy-flac-test.xml",
			"classpath:applicationContext-encoded-test.xml"
		};
	}
	
	public Initialiser getInitialiser() {
		return i_initialiser;
	}

	public void setInitialiser(Initialiser initialiser) {
		i_initialiser = initialiser;
	}

}
