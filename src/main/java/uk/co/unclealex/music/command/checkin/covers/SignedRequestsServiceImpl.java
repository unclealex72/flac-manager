package uk.co.unclealex.music.command.checkin.covers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import uk.co.unclealex.music.configuration.AmazonConfiguration;

import com.google.inject.Inject;
import com.sun.jersey.core.util.Base64;

/**
 * The default implementation of {@link SignedRequestsService}. This was copied
 * off the internet from somewhere.
 * 
 * @author alex
 * 
 */
public class SignedRequestsServiceImpl implements SignedRequestsService {

  /**
   * The charset to use whilst signing requests.
   */
  private static final String UTF8_CHARSET = "UTF-8";

  /**
   * The signing algorithm to use.
   */
  private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

  /**
   * The relative URI to send requests to.
   */
  private static final String REQUEST_URI = "/onca/xml";

  /**
   * The HTTP method used to talk with Amazon.
   */
  private static final String REQUEST_METHOD = "GET";

  /**
   * The endpoint used to talk to Amazon.
   */
  private final String awsEndpoint;

  /**
   * The public key used when talking to Amazon.
   */
  private final String awsAccessKeyId;

  /**
   * The private key used when talking to Amazon.
   */
  private final String awsSecretKey;

  /**
   * The {@link SecretKeySpec} used during request signing.
   */
  private SecretKeySpec secretKeySpec = null;

  /**
   * The {@link Mac} used during request signing.
   */
  private Mac mac = null;

  /**
   * Instantiates a new signed requests service impl.
   *
   * @param amazonConfiguration the amazon configuration
   */
  @Inject
  public SignedRequestsServiceImpl(AmazonConfiguration amazonConfiguration) {
    super();
    awsEndpoint = amazonConfiguration.getEndpoint();
    awsAccessKeyId = amazonConfiguration.getAccessKey();
    awsSecretKey = amazonConfiguration.getSecretKey();
  }

  /**
   * Initalise the {@link SecretKeySpec} and {@link Mac}.
   *
   * @throws UnsupportedEncodingException the unsupported encoding exception
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws InvalidKeyException the invalid key exception
   */
  @PostConstruct
  public void initialise() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
    byte[] secretyKeyBytes = getAwsSecretKey().getBytes(UTF8_CHARSET);
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
    Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
    mac.init(secretKeySpec);
    setSecretKeySpec(secretKeySpec);
    setMac(mac);
  }

  /**
   * {@inheritDoc}
   */
  public String signedUrl(Map<String, String> params) {
    SortedMap<String, String> sortedParamMap = new TreeMap<String, String>(params);
    sortedParamMap.put("AWSAccessKeyId", getAwsAccessKeyId());
    sortedParamMap.put("Timestamp", timestamp());

    String canonicalQS = canonicalize(sortedParamMap);
    String endpoint = getAwsEndpoint();
    String toSign = REQUEST_METHOD + "\n" + endpoint + "\n" + REQUEST_URI + "\n" + canonicalQS;

    String hmac = hmac(toSign);
    String sig = percentEncodeRfc3986(hmac);
    String url = "http://" + endpoint + REQUEST_URI + "?" + canonicalQS + "&Signature=" + sig;

    return url;
  }

  /**
   * Hmac.
   *
   * @param stringToSign the string to sign
   * @return the string
   */
  private String hmac(String stringToSign) {
    String signature = null;
    byte[] data;
    byte[] rawHmac;
    try {
      data = stringToSign.getBytes(UTF8_CHARSET);
      rawHmac = getMac().doFinal(data);
      signature = new String(Base64.encode(rawHmac)).trim();
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
    }
    return signature;
  }

  /**
   * Timestamp.
   *
   * @return the string
   */
  private String timestamp() {
    String timestamp = null;
    Calendar cal = Calendar.getInstance();
    DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
    timestamp = dfm.format(cal.getTime());
    return timestamp;
  }

  /**
   * Canonicalize.
   *
   * @param sortedParamMap the sorted param map
   * @return the string
   */
  private String canonicalize(SortedMap<String, String> sortedParamMap) {
    if (sortedParamMap.isEmpty()) {
      return "";
    }

    StringBuffer buffer = new StringBuffer();
    Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry<String, String> kvpair = iter.next();
      buffer.append(percentEncodeRfc3986(kvpair.getKey()));
      buffer.append("=");
      buffer.append(percentEncodeRfc3986(kvpair.getValue()));
      if (iter.hasNext()) {
        buffer.append("&");
      }
    }
    String cannoical = buffer.toString();
    return cannoical;
  }

  /**
   * Percent encode rfc3986.
   *
   * @param s the s
   * @return the string
   */
  private String percentEncodeRfc3986(String s) {
    String out;
    try {
      out = URLEncoder.encode(s, UTF8_CHARSET).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }
    catch (UnsupportedEncodingException e) {
      out = s;
    }
    return out;
  }

  /**
   * Gets the endpoint used to talk to Amazon.
   *
   * @return the endpoint used to talk to Amazon
   */
  public String getAwsEndpoint() {
    return awsEndpoint;
  }

  /**
   * Gets the public key used when talking to Amazon.
   *
   * @return the public key used when talking to Amazon
   */
  public String getAwsAccessKeyId() {
    return awsAccessKeyId;
  }

  /**
   * Gets the private key used when talking to Amazon.
   *
   * @return the private key used when talking to Amazon
   */
  public String getAwsSecretKey() {
    return awsSecretKey;
  }

  /**
   * Gets the {@link SecretKeySpec} used during request signing.
   *
   * @return the {@link SecretKeySpec} used during request signing
   */
  public SecretKeySpec getSecretKeySpec() {
    return secretKeySpec;
  }

  /**
   * Sets the {@link SecretKeySpec} used during request signing.
   *
   * @param secretKeySpec the new {@link SecretKeySpec} used during request signing
   */
  public void setSecretKeySpec(SecretKeySpec secretKeySpec) {
    this.secretKeySpec = secretKeySpec;
  }

  /**
   * Gets the {@link Mac} used during request signing.
   *
   * @return the {@link Mac} used during request signing
   */
  public Mac getMac() {
    return mac;
  }

  /**
   * Sets the {@link Mac} used during request signing.
   *
   * @param mac the new {@link Mac} used during request signing
   */
  public void setMac(Mac mac) {
    this.mac = mac;
  }

}