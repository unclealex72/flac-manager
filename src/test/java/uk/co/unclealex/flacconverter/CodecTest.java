/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author alex
 *
 */
public class CodecTest extends TestCase {

	public void testArtist(String artist, String expected) {
		Mp3FileCodec codec = new Mp3FileCodec(Arrays.asList(new String[] { "The" }));
		artist = codec.removeDefiniteArticle(artist);
		assertEquals(expected, artist);
	}
	
	public void testTheWho() {
		testArtist("The Who", "Who");
	}
	
	public void testThe() {
		testArtist("The", "The");
	}

	public void testTheloniousMonk() {
		testArtist("Thelonious Monk", "Thelonious Monk");
	}
}
