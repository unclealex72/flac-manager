package uk.co.unclealex.music;

import java.util.Map;


public interface PlaylistService {

	public Map<String, Iterable<String>> createPlaylists(String owner, Encoding encoding);

}
