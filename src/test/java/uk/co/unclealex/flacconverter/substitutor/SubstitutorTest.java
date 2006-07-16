/**
 * 
 */
package uk.co.unclealex.flacconverter.substitutor;

import junit.framework.TestCase;

/**
 * @author alex
 *
 */
public class SubstitutorTest extends TestCase {

	public void testSubstitutor() {
		Substitutor substitutor =
			new Substitutor("Test ${3:one} and ${two} but ${2:three} remembering ${two} but not ${4:one}");
		substitutor.substitute("one", "cateract");
		substitutor.substitute("two", "throng");
		substitutor.substitute("three", "seven");
		assertEquals(
				"Test cat and throng but se remembering throng but not cate",
				substitutor.getText());
	}
}
