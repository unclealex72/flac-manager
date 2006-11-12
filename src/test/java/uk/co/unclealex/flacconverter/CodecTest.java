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

	public void testArticles() {
		Mp3FileCodec codec = new Mp3FileCodec(Arrays.asList(new String[] { "The" }));
		String theWho = codec.removeDefiniteArticle("The Who");
		assertEquals("Who", theWho);
		String the = codec.removeDefiniteArticle("The");
		assertEquals("The", the);
		String theloniusMonk = codec.removeDefiniteArticle("Thelonious Monk");
		assertEquals("Thelonious Monk", theloniusMonk);
	}
}
