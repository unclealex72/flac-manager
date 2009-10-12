package uk.co.unclealex.music.albumcover.service;

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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class SignedRequestsServiceImpl implements SignedRequestsService {
  private static final String UTF8_CHARSET = "UTF-8";
  private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
  private static final String REQUEST_URI = "/onca/xml";
  private static final String REQUEST_METHOD = "GET";

  private String i_endpoint;
  private String i_awsAccessKeyId;
  private String i_awsSecretKey;

  private SecretKeySpec i_secretKeySpec = null;
  private Mac i_mac = null;

  public void initialise() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
    byte[] secretyKeyBytes = getAwsSecretKey().getBytes(UTF8_CHARSET);
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
    Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
    mac.init(secretKeySpec);
		setSecretKeySpec(secretKeySpec);
		setMac(mac);
  }

  /* (non-Javadoc)
	 * @see uk.co.unclealex.music.albumcover.service.SignedRequestsService#sign(java.util.Map)
	 */
  public String sign(Map<String, String> params) {
    SortedMap<String, String> sortedParamMap =
      new TreeMap<String, String>(params);
    sortedParamMap.put("AWSAccessKeyId", getAwsAccessKeyId());
    sortedParamMap.put("Timestamp", timestamp());

    String canonicalQS = canonicalize(sortedParamMap);
    String endpoint = getEndpoint();
		String toSign =
      REQUEST_METHOD + "\n"
      + endpoint + "\n"
      + REQUEST_URI + "\n"
      + canonicalQS;

    String hmac = hmac(toSign);
    String sig = percentEncodeRfc3986(hmac);
    String url = "http://" + endpoint + REQUEST_URI + "?" +
    canonicalQS + "&Signature=" + sig;

    return url;
  }

  private String hmac(String stringToSign) {
    String signature = null;
    byte[] data;
    byte[] rawHmac;
    try {
      data = stringToSign.getBytes(UTF8_CHARSET);
      rawHmac = getMac().doFinal(data);
      Base64 encoder = new Base64();
      signature = new String(encoder.encode(rawHmac)).trim();
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
    }
    return signature;
  }

  private String timestamp() {
    String timestamp = null;
    Calendar cal = Calendar.getInstance();
    DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
    timestamp = dfm.format(cal.getTime());
    return timestamp;
  }

  private String canonicalize(SortedMap<String, String> sortedParamMap) {
    if (sortedParamMap.isEmpty()) {
      return "";
    }

    StringBuffer buffer = new StringBuffer();
    Iterator<Map.Entry<String, String>> iter =
      sortedParamMap.entrySet().iterator();

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

  private String percentEncodeRfc3986(String s) {
    String out;
    try {
      out = URLEncoder.encode(s, UTF8_CHARSET)
      .replace("+", "%20")
      .replace("*", "%2A")
      .replace("%7E", "~");
    } catch (UnsupportedEncodingException e) {
      out = s;
    }
    return out;
  }

	public String getEndpoint() {
		return i_endpoint;
	}

	public void setEndpoint(String endpoint) {
		i_endpoint = endpoint;
	}

	public String getAwsAccessKeyId() {
		return i_awsAccessKeyId;
	}

	public void setAwsAccessKeyId(String awsAccessKeyId) {
		i_awsAccessKeyId = awsAccessKeyId;
	}

	public String getAwsSecretKey() {
		return i_awsSecretKey;
	}

	public void setAwsSecretKey(String awsSecretKey) {
		i_awsSecretKey = awsSecretKey;
	}

	public SecretKeySpec getSecretKeySpec() {
		return i_secretKeySpec;
	}

	public void setSecretKeySpec(SecretKeySpec secretKeySpec) {
		i_secretKeySpec = secretKeySpec;
	}

	public Mac getMac() {
		return i_mac;
	}

	public void setMac(Mac mac) {
		i_mac = mac;
	}

}