package uk.co.unclealex.music.covers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.AbstractFilter;
import org.jdom2.filter.Filter;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * The {@link ArtworkSearchingService} that uses Amazon's AWS web service.
 * @author alex
 *
 */
public class AmazonArtworkSearchingService implements ArtworkSearchingService {

  private static final Logger log = LoggerFactory.getLogger(AmazonArtworkSearchingService.class);

  /**
   * The {@link SignedRequestsService} used to generate signed Amazon URLs.
   */
  private final SignedRequestsService signedRequestsService;

  @Inject
  protected AmazonArtworkSearchingService(SignedRequestsService signedRequestsService) {
    super();
    this.signedRequestsService = signedRequestsService;
  }

  /**
   * {@inheritDoc}
   */
  public URI findArtwork(String asin) throws IOException {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("Service", "AWSECommerceService");
    parameters.put("Operation", "ItemLookup");
    parameters.put("ItemId", asin);
    parameters.put("ResponseGroup", "Images");
    parameters.put("AssociateTag", "dummy");
    String signedUrl = getSignedRequestsService().signedUrl(parameters);
    log.info(String.format("Looking for covers for %s", asin));
    log.debug("Calling Amazon with URL " + signedUrl);
    Document doc;
    try {
      doc = new SAXBuilder().build(new URL(signedUrl));
    }
    catch (JDOMException e) {
      throw new IOException("Could not understand the amazon response to URL " + signedUrl, e);
    }
    List<ExternalCoverArtImage> externalCoverArtImages = Lists.newArrayList();
    for (Element el : descendantsOf(doc)) {
      Namespace ns = el.getNamespace();
      if (el.getName().endsWith("Image")) {
        Element urlElement;
        Element heightElement;
        Element widthElement;
        if ((urlElement = el.getChild("URL", ns)) != null
            && (heightElement = el.getChild("Height", ns)) != null
            && (widthElement = el.getChild("Width", ns)) != null) {
          String url = Strings.nullToEmpty(urlElement.getText()).trim();
          String height = Strings.nullToEmpty(heightElement.getText()).trim();
          String width = Strings.nullToEmpty(widthElement.getText()).trim();
          try {
            externalCoverArtImages.add(new ExternalCoverArtImage(new URI(url), Integer.parseInt(height)
                * Integer.parseInt(width)));
          }
          catch (NumberFormatException e) {
            log.warn("Ignoring element with an invalid number format.", e);
          }
          catch (URISyntaxException e) {
            log.warn("Ignoring element with an invalid URI.", e);
          }
        }
      }
    }
    Comparator<ExternalCoverArtImage> largestFirstComparator = new Comparator<ExternalCoverArtImage>() {

      @Override
      public int compare(ExternalCoverArtImage o1, ExternalCoverArtImage o2) {
        return o2.getSize() - o1.getSize();
      }
    };
    Collections.sort(externalCoverArtImages, largestFirstComparator);
    return externalCoverArtImages.isEmpty() ? null : externalCoverArtImages.get(0).getUri();
  }

  /**
   * Get all elements in a document.
   * @param doc The document to search.
   * @return All elements in a document.
   */
  protected Iterable<Element> descendantsOf(Document doc) {
    Filter<Element> filter = new AbstractFilter<Element>() {

      @Override
      public Element filter(Object content) {
        if (content instanceof Element) {
          return (Element) content;
        }
        else {
          return null;
        }
      }
    };
    return doc.getDescendants(filter);
  }

  public SignedRequestsService getSignedRequestsService() {
    return signedRequestsService;
  }
}
