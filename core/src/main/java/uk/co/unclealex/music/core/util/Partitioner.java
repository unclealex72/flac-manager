package uk.co.unclealex.music.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Partitioner<E> {

	public List<Collection<E>> partition(Collection<E> collection, int maximumSize) {
		if (maximumSize < 1) {
			return Collections.singletonList(collection);
		}
		List<Collection<E>> partitions = new LinkedList<Collection<E>>();
		int cnt = 0;
		List<E> partition = null;
		for (E el : collection) {
			if (cnt++ % maximumSize == 0) {
				partition = new LinkedList<E>();
				partitions.add(partition);
			}
			partition.add(el);
		}
		return partitions;
	}
}
