package uk.co.unclealex.music.albumcover.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;

import uk.co.unclealex.music.base.model.ExternalCoverArtImage;
import uk.co.unclealex.music.base.service.ExternalCoverArtException;
import uk.co.unclealex.music.base.service.ExternalCoverArtService;

public class AmazonExternalCoverArtService implements ExternalCoverArtService {

	private static final Logger log = Logger.getLogger(AmazonExternalCoverArtService.class);
	
	private SignedRequestsService i_signedRequestsService;
	
	@Override
	public SortedSet<ExternalCoverArtImage> searchForImages(String artist, String album) throws ExternalCoverArtException, IOException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("Service", "AWSECommerceService");
		parameters.put("Operation", "ItemSearch");
		parameters.put("SearchIndex", "Music");
		parameters.put("Artist", artist);
		parameters.put("Title", album);
		parameters.put("ResponseGroup", "Images");
		String signedUrl = getSignedRequestsService().sign(parameters);
		log.info("Calling Amazon with URL " + signedUrl);
		Document doc;
		try {
			doc = new SAXBuilder().build(new URL(signedUrl));
		}
		catch (JDOMException e) {
			throw new ExternalCoverArtException("Could not understand the amazon response to URL " + signedUrl, e);
		}
		Map<Parent, ExternalCoverArtImage> imagesByParentElement = new HashMap<Parent, ExternalCoverArtImage>();
		for (Iterator<Element> iter = descendantsOf(doc); iter.hasNext(); ) {
			Element el = iter.next();
			Namespace ns = el.getNamespace();
			if (el.getName().endsWith("Image")) {
				Element urlElement;
				Element heightElement;
				Element widthElement;
				if (
						(urlElement = el.getChild("URL", ns)) != null && 
						(heightElement = el.getChild("Height", ns)) != null &&
						(widthElement = el.getChild("Width", ns)) != null) {
					String url = StringUtils.trimToEmpty(urlElement.getText());
					String height = StringUtils.trimToEmpty(heightElement.getText());
					String width = StringUtils.trimToEmpty(widthElement.getText());
					try {
						ExternalCoverArtImage newExternalCoverArtImage = new ExternalCoverArtImage(new URL(url), Long.parseLong(height) * Long.parseLong(width));
						Parent parent = el.getParent();
						ExternalCoverArtImage existingExternalCoverArtImage = imagesByParentElement.get(parent);
						if (existingExternalCoverArtImage == null || newExternalCoverArtImage.compareTo(existingExternalCoverArtImage) > 0) {
							imagesByParentElement.put(parent, newExternalCoverArtImage);
						}
					}
					catch (NumberFormatException e) {
						log.warn("Ignoring element with an invalid number format.", e);
					}
					catch (MalformedURLException e) {
						log.warn("Ignoring element with a malformed URL.", e);						
					}
				}
			}
		}
		return new TreeSet<ExternalCoverArtImage>(imagesByParentElement.values());
	}

	@SuppressWarnings("unchecked")
	protected Iterator<Element> descendantsOf(Document doc) {
		Filter filter = new Filter() {
			@Override
			public boolean matches(Object obj) {
				return obj instanceof Element;
			}
		};
		return doc.getDescendants(filter);
	}

	public SignedRequestsService getSignedRequestsService() {
		return i_signedRequestsService;
	}

	public void setSignedRequestsService(SignedRequestsService signedRequestsService) {
		i_signedRequestsService = signedRequestsService;
	}

}
