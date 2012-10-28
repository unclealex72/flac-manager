package uk.co.unclealex.music.checkin.covers;

import java.util.Map;

/**
 * An interface that is used to sign requests to Amazon.
 */
public interface SignedRequestsService {

  /**
   * Sign an amazon request.
   * @param params The parameters being sent to the request.
   * @return The full Amazon URL to call.
   */
	public String signedUrl(Map<String, String> params);

}