package uk.co.unclealex.music.flac;

import uk.co.unclealex.music.SpringTest;

public abstract class FlacSpringTest extends SpringTest {

	@Override
	protected String[] getConfigLocations() {
		return new String[] {
			"classpath:applicationContext.xml",
			"classpath:applicationContext-encoded-test.xml",
			"classpath:applicationContext-real-flac-test.xml"};
	}	
}
