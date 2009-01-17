package uk.co.unclealex.music.albumcover.service;

import com.amazon.webservices.awsecommerceservice.AWSECommerceServicePortType;

public interface AmazonService extends AWSECommerceServicePortType {

	public String getAccessKey();

	public void setAccessKey(String accessKey);

	public String getSubscriberId();

	public void setSubscriberId(String subscriberId);

}
