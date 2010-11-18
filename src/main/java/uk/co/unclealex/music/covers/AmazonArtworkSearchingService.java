package uk.co.unclealex.music.covers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;

import uk.co.unclealex.music.LatinService;

public class AmazonArtworkSearchingService implements ArtworkSearchingService {

	private static final Logger log = Logger.getLogger(AmazonArtworkSearchingService.class);
	
	private SignedRequestsService i_signedRequestsService;
	private LatinService i_latinService;
	
	@Override
	public List<String> findArtwork(File flacFile) throws IOException {
		AudioFile audioFile;
		try {
			audioFile = AudioFileIO.read(flacFile);
		}
		catch (CannotReadException e) {
			throw new IOException("Cannot read tag information from flac file " + flacFile, e);
		}
		catch (TagException e) {
			throw new IOException("Cannot read tag information from flac file " + flacFile, e);
		}
		catch (ReadOnlyFileException e) {
			throw new IOException("Cannot read tag information from flac file " + flacFile, e);
		}
		catch (InvalidAudioFrameException e) {
			throw new IOException("Cannot read tag information from flac file " + flacFile, e);
		}
		Tag tag = audioFile.getTag();
		String artist = tag.getFirst(FieldKey.ARTIST);
		String album = tag.getFirst(FieldKey.ALBUM);
		return findArtwork(artist, album);
	}
	
	public List<String> findArtwork(String artist, String album) throws IOException {
		LatinService latinService = getLatinService();
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("Service", "AWSECommerceService");
		parameters.put("Operation", "ItemSearch");
		parameters.put("SearchIndex", "Music");
		parameters.put("Artist", latinService.removeCommonAccents(artist));
		parameters.put("Title", latinService.removeCommonAccents(album));
		parameters.put("ResponseGroup", "Images");
		String signedUrl = getSignedRequestsService().sign(parameters);
		log.info(String.format("Looking for covers for %s: %s", artist, album));
		log.debug("Calling Amazon with URL " + signedUrl);
		Document doc;
		try {
			doc = new SAXBuilder().build(new URL(signedUrl));
		}
		catch (JDOMException e) {
			throw new IOException("Could not understand the amazon response to URL " + signedUrl, e);
		}
		Map<Parent, ExternalCoverArtImage> imagesByParentElement = new LinkedHashMap<Parent, ExternalCoverArtImage>();
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
		Transformer<ExternalCoverArtImage, String> transformer = new Transformer<ExternalCoverArtImage, String>() {
			@Override
			public String transform(ExternalCoverArtImage externalCoverArtImage) {
				return externalCoverArtImage.getUrl().toString();
			}
		};
		return CollectionUtils.collect(imagesByParentElement.values(), transformer, new ArrayList<String>());
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

	public LatinService getLatinService() {
		return i_latinService;
	}

	public void setLatinService(LatinService latinService) {
		i_latinService = latinService;
	}

}
