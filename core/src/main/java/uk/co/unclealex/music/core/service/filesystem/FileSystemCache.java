package uk.co.unclealex.music.core.service.filesystem;

import java.util.Collection;

public interface FileSystemCache {

	public void createCache(Collection<String> titleFormats);
	
	public PathInformationBean findPath(String path);
}
