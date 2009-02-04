package uk.co.unclealex.music.encoder;

import uk.co.unclealex.music.encoder.dao.TestFlacProvider;

public abstract class FlacSpringTest extends SpringTest {

	private TestFlacProvider i_testFlacProvider;
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] {
			"classpath*:applicationContext-music-album-covers.xml", "classpath*:applicationContext-music-core.xml", 
			"classpath*:applicationContext-music-core-test.xml", "classpath*:applicationContext-music-encoder-flac-test.xml"
		};
	}

	public TestFlacProvider getTestFlacProvider() {
		return i_testFlacProvider;
	}

	public void setTestFlacProvider(TestFlacProvider testFlacProvider) {
		i_testFlacProvider = testFlacProvider;
	}

}
