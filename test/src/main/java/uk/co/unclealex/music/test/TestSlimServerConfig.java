package uk.co.unclealex.music.test;

import java.util.List;

import uk.co.unclealex.music.base.SlimServerConfig;

public class TestSlimServerConfig implements SlimServerConfig {
	
	private List<String> i_definiteArticles;
	private TestFlacProvider i_testFlacProvider;
	
	public List<String> getDefiniteArticles() {
		return i_definiteArticles;
	}

	@Override
	public String getRootDirectory() {
		return getTestFlacProvider().getWorkingDirectory().getAbsolutePath();
	}

	public void setDefiniteArticles(List<String> definiteArticles) {
		i_definiteArticles = definiteArticles;
	}

	public TestFlacProvider getTestFlacProvider() {
		return i_testFlacProvider;
	}

	public void setTestFlacProvider(TestFlacProvider testFlacProvider) {
		i_testFlacProvider = testFlacProvider;
	}
}
