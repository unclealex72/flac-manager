package uk.co.unclealex.music;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;
import org.springframework.test.AbstractTransactionalSpringContextTests;

public abstract class SpringTest extends AbstractTransactionalSpringContextTests {

	public SpringTest() {
		setAutowireMode(AUTOWIRE_BY_NAME);
		setDependencyCheck(false);
	}
	
	@SuppressWarnings("unchecked")
	public void assertEquals(String message, Collection expected, Collection actual) {
		List extra = new LinkedList();
		extra.addAll(actual);
		extra.removeAll(expected);
		List missing = new LinkedList();
		missing.addAll(expected);
		missing.removeAll(actual);
		if (extra.isEmpty() && missing.isEmpty()) {
			return;
		}
		Transformer transformer = new Transformer() {
			@Override
			public Object transform(Object input) {
				return input.toString();
			}
		};
		SortedSet extraSet = new TreeSet();
		CollectionUtils.collect(extra, transformer, extraSet);
		SortedSet missingSet = new TreeSet();
		CollectionUtils.collect(missing, transformer, missingSet);

		String error = "";
		if (!extra.isEmpty()) {
			error = "The following extra elements were found:\n  " + StringUtils.join(extraSet.iterator(), "\n  ");
		}
		if (!missing.isEmpty()) {
			if (!extra.isEmpty()) {
				error += "\n";
			}
			error += "The following expected elements were missing:\n  " + StringUtils.join(missingSet.iterator(), "\n  ");
		}
		fail(message + "\n" + error);
	}
	
	public void assertEquals(String message, Map<?,?> expected, Map<?,?> actual) {
		SortedSet<Object> incorrectKeys = new TreeSet<Object>();
		for (Map.Entry<?, ?> actualEntry : actual.entrySet()) {
			Object key = actualEntry.getKey();
			if (!actualEntry.getValue().equals(expected.get(key))) {
				incorrectKeys.add(key);
			}
		}
		SortedSet<Object> extraKeys = new TreeSet<Object>(actual.keySet());
		extraKeys.removeAll(expected.keySet());
		SortedSet<Object> missingKeys = new TreeSet<Object>(expected.keySet());
		missingKeys.removeAll(actual.keySet());
		
		if (incorrectKeys.isEmpty() && missingKeys.isEmpty() && extraKeys.isEmpty()) {
			return;
		}
		List<StringBuffer> errors = new LinkedList<StringBuffer>();
		if (!incorrectKeys.isEmpty()) {
			StringBuffer error = new StringBuffer();
			error.append("The following keys were incorrect:\n");
			for (Object key : incorrectKeys) {
				error.append("  ").
					append(key).
					append(": expected ").
					append(expected.get(key)).
					append(" but was ").
					append(actual.get(key)).
					append('\n');
			}
			errors.add(error);
		}
		if (!missingKeys.isEmpty()) {
			StringBuffer error = new StringBuffer();
			error.append("The following keys were missing:\n");
			for (Object key : missingKeys) {
				error.append("  ").append(key).append(": ").append(expected.get(key)).append('\n');
			}
		}
		if (!extraKeys.isEmpty()) {
			StringBuffer error = new StringBuffer();
			error.append("The following extra keys were found:\n");
			for (Object key : extraKeys) {
				error.append("  ").append(key).append(": ").append(actual.get(key)).append('\n');
			}
		}
		fail(message + "\n" + StringUtils.join(errors.iterator(), '\n'));
	}
	@Override
	protected abstract String[] getConfigLocations();
}
