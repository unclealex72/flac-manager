package uk.co.unclealex.music.albumcover.service;

import java.util.Map;

public interface SignedRequestsService {

	public String sign(Map<String, String> params);

}