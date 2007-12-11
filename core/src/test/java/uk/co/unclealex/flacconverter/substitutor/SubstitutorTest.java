/**
 * 
 */
package uk.co.unclealex.flacconverter.substitutor;

import uk.co.unclealex.music.core.service.titleformat.TitleFormatVariable;
import uk.co.unclealex.music.core.substitutor.Substitutor;
import junit.framework.TestCase;

/**
 * @author alex
 *
 */
public class SubstitutorTest extends TestCase {

	public void testSubstitutor() {
		Substitutor substitutor =
			new Substitutor(
					"Test ${3:artist} and ${album} but ${2:title} remembering ${album} but not ${4:artist}, dude${2:track}");
		substitutor.substitute(TitleFormatVariable.ARTIST, "cateract");
		substitutor.substitute(TitleFormatVariable.ALBUM, "throng");
		substitutor.substitute(TitleFormatVariable.TITLE, "seven");
		substitutor.substitute(TitleFormatVariable.TRACK, 4);
		assertEquals(
				"Test cat and throng but se remembering throng but not cate, dude04",
				substitutor.getText());
	}
}
