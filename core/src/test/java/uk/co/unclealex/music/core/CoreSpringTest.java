package uk.co.unclealex.music.core;

public class CoreSpringTest extends SpringTest {

	@Override
	protected String[] getConfigLocations() {
		return new String[] { "applicationContext-music-core.xml", "applicationContext-music-core.xml" }; 
	}

}
