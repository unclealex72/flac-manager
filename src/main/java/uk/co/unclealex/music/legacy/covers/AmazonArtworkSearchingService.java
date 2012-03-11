package uk.co.unclealex.music.legacy.covers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.legacy.LatinService;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class AmazonArtworkSearchingService implements ArtworkSearchingService {

	private static final Logger log = LoggerFactory.getLogger(AmazonArtworkSearchingService.class);
	
	private SignedRequestsService i_signedRequestsService;
	private LatinService i_latinService;
	
	@Inject
	protected AmazonArtworkSearchingService(SignedRequestsService signedRequestsService, LatinService latinService) {
		super();
		i_signedRequestsService = signedRequestsService;
		i_latinService = latinService;
	}

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
					String url = Strings.nullToEmpty(urlElement.getText()).trim();
					String height = Strings.nullToEmpty(heightElement.getText()).trim();
					String width = Strings.nullToEmpty(widthElement.getText()).trim();
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
		Function<ExternalCoverArtImage, String> function = new Function<ExternalCoverArtImage, String>() {
			@Override
			public String apply(ExternalCoverArtImage externalCoverArtImage) {
				return externalCoverArtImage.getUrl().toString();
			}
		};
		return Lists.newArrayList(Iterables.transform(imagesByParentElement.values(), function));
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

	public LatinService getLatinService() {
		return i_latinService;
	}
}
