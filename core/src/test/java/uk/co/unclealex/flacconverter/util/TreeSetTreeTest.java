package uk.co.unclealex.flacconverter.util;

import java.util.ArrayList;
import java.util.List;

import uk.co.unclealex.music.core.util.TreeSetTree;

import junit.framework.TestCase;

public class TreeSetTreeTest extends TestCase {

	
	private static final String[] WORDS = new String[] { "marble", "make", "bark", "bite" };

	public void testTree() {
		TreeSetTree<Character> tree = new TreeSetTree<Character>(null);
		for (String word : WORDS) {
			tree.createPath(extractLetters(word).iterator());
		}
		for (String word : WORDS) {
			for (int len = 0; len < word.length(); len++) {
				String str = word.substring(0, len);
				List<Character> letters = extractLetters(str);
				assertNotNull("The string '" + str + "' could not be found", tree.traverse(letters.iterator()));
				str = word.substring(0, len) + "x";
				letters = extractLetters(str);
				assertNull("The string '" + str + "' should not have been found", tree.traverse(letters.iterator()));
			}
		}
	}
	
	protected List<Character> extractLetters(String word) {
		List<Character> letters = new ArrayList<Character>();
		for (char letter : word.toCharArray()) {
			letters.add(letter);
		}
		return letters;
	}
}
