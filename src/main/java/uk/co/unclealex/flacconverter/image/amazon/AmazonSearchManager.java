/**
 * 
 */
package uk.co.unclealex.flacconverter.image.amazon;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.xfire.XFireRuntimeException;

import uk.co.unclealex.flacconverter.image.Album;
import uk.co.unclealex.flacconverter.image.SearchManager;

import com.amazon.webservices.AWSECommerceServiceClient;
import com.amazon.webservices.AWSECommerceServicePortType;
import com.amazon.webservices.awsecommerceservice._2006_11_14.Image;
import com.amazon.webservices.awsecommerceservice._2006_11_14.Item;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ItemAttributes;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ItemSearch;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ItemSearchResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.Items;
import com.amazon.webservices.awsecommerceservice._2006_11_14.Tracks.Disc;
import com.amazon.webservices.awsecommerceservice._2006_11_14.Tracks.Disc.Track;

/**
 * @author alex
 *
 */
public class AmazonSearchManager implements SearchManager {

	private static final long WAIT_BETWEEN_REQUESTS = 1000;
	private static final Logger log = Logger.getLogger(AmazonSearchManager.class);
	
	private Date i_lastSearchTime;
	private AWSECommerceServicePortType i_portType =
		new AWSECommerceServiceClient().getAWSECommerceServicePort();
	
	private synchronized Items search(String artist, String title, BigInteger page) {
		ItemSearchRequest request = new ItemSearchRequest();
		request.setArtist(artist);
		request.setTitle(title);
		request.setItemPage(page);
		request.setSearchIndex("Music");
		request.getResponseGroup().add("Large");
		ItemSearch search = new ItemSearch();
		search.setAWSAccessKeyId("1GK0A6KVNQEFVH5Q1Z82");
		search.setShared(request);
		
		if (i_lastSearchTime != null) {
			boolean needToWait = true;
			while (needToWait) {
				long millisSinceLastSearch = new Date().getTime() - i_lastSearchTime.getTime();
				if (millisSinceLastSearch < WAIT_BETWEEN_REQUESTS) {
					try {
						wait(WAIT_BETWEEN_REQUESTS - millisSinceLastSearch);
						needToWait = false;
					} catch (InterruptedException e) {
						// we need to keep waiting as there should not have been an interruption.
					}
				}
				else {
					needToWait = false;
				}
			}
		}
		i_lastSearchTime = new Date();
		boolean keepGoing = true;
		ItemSearchResponse response = null;
		while (keepGoing) {
			try {
				response = i_portType.itemSearch(search);
				keepGoing = false;
			}
			catch (XFireRuntimeException e) {
				log.error(e);
			}
		}
		List<Items> items = response.getItems();
		return items.size() == 0?null:items.get(0);
	}
	
	public Collection<Album> search(String artist, String title) {
		BigInteger totalPages = null;
		BigInteger currentPage = new BigInteger("1");
		Collection<Album> albums = new LinkedList<Album>();
		while (totalPages == null || currentPage.intValue() <= totalPages.intValue()) {
			Items items = search(artist, title, currentPage);
			totalPages = items.getTotalPages();
			for (Item item : items.getItem()) {
				albums.add(createAlbum(item));
			}
			currentPage = currentPage.add(BigInteger.ONE);
		}
		return albums;
	}

	protected Album createAlbum(Item item) {
		List<String> tracks = new LinkedList<String>();
		if (item.getTracks() != null) {
			List<Disc> discs = item.getTracks().getDisc();
			if (discs != null) {
				for (Disc disc : discs) {
					List<Track> discTracks = disc.getTrack();
					if (discTracks != null) {
						for (Track track : discTracks) {
							tracks.add(track.getValue());
						}
					}
				}
			}
		}
		Image image = item.getLargeImage();
		String imageUrl = null;
		int area = 0;
		if (image == null || image.getURL() == null) {
			image = item.getMediumImage();
			if (image == null || image.getURL() == null) {
				image = item.getSmallImage();
			}
		}
		if (image != null && image.getURL() != null) {
			imageUrl = image.getURL();
			area = image.getWidth().getValue().multiply(image.getHeight().getValue()).intValue();
		}
		
		ItemAttributes attr = item.getItemAttributes();
		return new Album(attr.getArtist(), attr.getTitle(), imageUrl, area, tracks);
	}

}
