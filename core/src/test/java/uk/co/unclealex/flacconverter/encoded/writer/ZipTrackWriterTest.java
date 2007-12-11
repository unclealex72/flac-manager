package uk.co.unclealex.flacconverter.encoded.writer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipEntry;

import junit.framework.TestCase;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.music.core.encoded.writer.ZipTrackStreamImpl;

public class ZipTrackWriterTest extends TestCase {

	public void testSimpleEntry() {
		checkEntries("something.mpg", new String[] { "something.mpg" });
	}
	
	public void testDirectoriesCreatedOnlyOnce() {
		ZipTrackStreamImpl stream = new ZipTrackStreamImpl();
		checkEntries(
				stream,
				"Slayer/South Of Heaven/01 - South Of Heaven.mpg",
				new String[] { "Slayer/", "Slayer/South Of Heaven/", "Slayer/South Of Heaven/01 - South Of Heaven.mpg" });
		checkEntries(
				stream,
				"Slayer/South Of Heaven/02 - Silent Scream.mpg",
				new String[] { "Slayer/South Of Heaven/02 - Silent Scream.mpg" });
	}
	
	public void checkEntries(String title, String[] expectedEntries) {
		checkEntries(new ZipTrackStreamImpl(), title, expectedEntries);
	}
	
	public void checkEntries(ZipTrackStreamImpl zipTrackStreamImpl, String title, String[] expectedEntries) {
		List<ZipEntry> zipEntries = zipTrackStreamImpl.createEntries(title);
		int lastIndex = zipEntries.size() - 1;
		for (ListIterator<ZipEntry> iter = zipEntries.listIterator(); iter.hasNext(); ) {
			int index = iter.nextIndex();
			boolean isDirectory = iter.next().isDirectory();
			if (index == lastIndex) {
				assertFalse("The last entry added was a directory.", isDirectory);
			}
			else {
				assertTrue("An entry not at the end of a list was not a directory.", isDirectory);
			}
		}
		List<String> entries = new LinkedList<String>();
		CollectionUtils.collect(
				zipEntries,
				new Transformer<ZipEntry, String>() {
					@Override
					public String transform(ZipEntry zipEntry) {
						return zipEntry.getName();
					}
				},
				entries);
		assertEquals("The wrong entries were created.", Arrays.asList(expectedEntries), entries);
	}
}
