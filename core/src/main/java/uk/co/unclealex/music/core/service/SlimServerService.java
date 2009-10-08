package uk.co.unclealex.music.core.service;

import java.util.List;

public interface SlimServerService {

	public boolean isScanning();

	public List<String> getDefiniteArticles();
	
	public String makePathRelative(String path);

	public String makePathAbsolute(String path);
}
