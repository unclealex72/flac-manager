package uk.co.unclealex.music.legacy;

import java.util.Map;


public interface PlaylistService {

	public Map<String, Iterable<String>> createPlaylists(String owner, Encoding encoding);

}
