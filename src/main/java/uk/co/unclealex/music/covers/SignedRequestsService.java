package uk.co.unclealex.music.covers;

import java.util.Map;

public interface SignedRequestsService {

	public String sign(Map<String, String> params);

}