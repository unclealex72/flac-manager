package uk.co.unclealex.music.legacy.covers;

import java.util.Map;

public interface SignedRequestsService {

	public String sign(Map<String, String> params);

}