package uk.co.unclealex.flacconverter.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.co.unclealex.flacconverter.Partitioner;
import uk.co.unclealex.music.core.EncodedSpringTest;

public class PartitionerTest extends EncodedSpringTest {

	private Partitioner<String> i_partitioner; 
	
	public void testEmpty() {
		String[] input = {};
		String[][] noLimitExpected = { input };
		String[][] limitExpected = new String[0][0];
		test(input, noLimitExpected, -1);
		test(input, noLimitExpected, 0);
		test(input, limitExpected, 1);
	}

	public void testNoLimit() {
		String[] original = { "1", "2", "3", "4" };
		String[][] expected = { original };
		test(original, expected, 0);
		test(original, expected, -1);
	}
	
	public void testSmallLimited() {
		String[] original = { "1", "2", "3", "4" };
		String[][] expected = { { "1", "2", "3" }, { "4" }};
		test(original, expected, 3);
	}

	public void testLargeLimited() {
		String[] original = { "1", "2", "3", "4" };
		String[][] expected = { original };
		test(original, expected, 5);
	}

	private void test(String[] input, String[][] expected, int maximumSize) {
		List<List<String>> expectedLists = new LinkedList<List<String>>();
		for (String[] el : expected) {
			expectedLists.add(Arrays.asList(el));
		}
		List<String> inputList = Arrays.asList(input);
		List<Collection<String>> actual = getPartitioner().partition(inputList, maximumSize);
		assertEquals(
			"Incorrect partitions recorded for values " + inputList + " with maximum size " + maximumSize,
			expectedLists, actual);
	}

	public Partitioner<String> getPartitioner() {
		return i_partitioner;
	}

	public void setPartitioner(Partitioner<String> partitioner) {
		i_partitioner = partitioner;
	}
}
