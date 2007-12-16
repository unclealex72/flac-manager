package uk.co.unclealex.music.core;

import java.util.List;

public class TestSlimServerConfig implements SlimServerConfig {
	
	private List<String> i_definiteArticles;

	public List<String> getDefiniteArticles() {
		return i_definiteArticles;
	}

	public void setDefiniteArticles(List<String> definiteArticles) {
		i_definiteArticles = definiteArticles;
	}
	

}
