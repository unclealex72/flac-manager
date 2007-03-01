import java.math.BigInteger;

import uk.co.unclealex.flacconverter.image.Album;
import uk.co.unclealex.flacconverter.image.SearchManager;
import uk.co.unclealex.flacconverter.image.amazon.AmazonSearchManager;

import com.amazon.webservices.AWSECommerceServiceClient;
import com.amazon.webservices.AWSECommerceServicePortType;
import com.amazon.webservices.awsecommerceservice._2006_11_14.Item;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ItemAttributes;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ItemSearch;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ItemSearchResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.Items;
import com.amazon.webservices.awsecommerceservice._2006_11_14.Tracks.Disc;
import com.amazon.webservices.awsecommerceservice._2006_11_14.Tracks.Disc.Track;

/**
 * 
 */

/**
 * @author alex
 *
 */
public class Amazon {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SearchManager manager = new AmazonSearchManager();
		for (Album album : manager.search("Napalm Death", "Scum")) {
			System.out.println(album);
		}
	}
	
	public static void oldmain(String[] args) {
		AWSECommerceServiceClient client = new AWSECommerceServiceClient();
		AWSECommerceServicePortType portType = client.getAWSECommerceServicePort();
		ItemSearchRequest request = new ItemSearchRequest();
		request.setArtist("Queen");
		request.setSearchIndex("Music");
		request.getResponseGroup().add("Large");
		ItemSearch search = new ItemSearch();
		search.setAWSAccessKeyId("1GK0A6KVNQEFVH5Q1Z82");
		search.setShared(request);
		int page = 1;
		int totalPages = 1;
		int artistsFound;
		do {
			artistsFound = 0;
			search.getShared().setItemPage(new BigInteger(new Integer(page).toString()));
			ItemSearchResponse response = portType.itemSearch(search);
			for (Items items : response.getItems()) {
				totalPages = items.getTotalPages().intValue();
				System.out.println("Page " + page + " of " + totalPages);
				for (Item item : items.getItem()) {
					if (printItem(item)) {
						artistsFound++;
					}
				}
			}			
		} while (page++ <= totalPages && artistsFound != 0);
	}

	public static boolean printItem(Item item) {
		ItemAttributes attributes = item.getItemAttributes();
		for (String artist : attributes.getArtist()) {
			if (artist.equalsIgnoreCase("queen")) {
				System.out.println(artist + " - " + attributes.getTitle());
				for (Disc disc : item.getTracks().getDisc()) {
					for (Track track : disc.getTrack()) {
						System.out.println("    " + track.getValue());
					}
				}
				return true;
			}
		}
		return false;
	}
}
