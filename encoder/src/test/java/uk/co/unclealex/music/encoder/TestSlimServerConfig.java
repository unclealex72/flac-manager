package uk.co.unclealex.music.encoder;

import java.util.List;

import uk.co.unclealex.music.core.SlimServerConfig;

public class TestSlimServerConfig implements SlimServerConfig {
	
	private List<String> i_definiteArticles;

	public List<String> getDefiniteArticles() {
		return i_definiteArticles;
	}

	public void setDefiniteArticles(List<String> definiteArticles) {
		i_definiteArticles = definiteArticles;
	}
	

}
