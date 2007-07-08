package uk.co.unclealex.flacconverter.encoded.writer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;

public class ZipTrackWriter extends AbstractTrackWriter<ZipOutputStream> {

	private static final String DIR_SEPARATOR_UNIX_STRING = new String(new char[] {IOUtils.DIR_SEPARATOR_UNIX});
	
	private ZipOutputStream i_zipOutputStream;
	private List<String> i_directories = new LinkedList<String>();
	
	@Override
	public ZipOutputStream createStream(EncodedTrackBean encodedTrackBean, String title) throws IOException {
		ZipOutputStream zipOutputStream = getZipOutputStream();
		for (ZipEntry entry : createEntries(title)) {
			zipOutputStream.putNextEntry(entry);
		}
		return zipOutputStream;
	}

	protected List<ZipEntry> createEntries(String title) {
		title = title.replace(File.pathSeparatorChar, IOUtils.DIR_SEPARATOR_UNIX);
		if (title.startsWith(DIR_SEPARATOR_UNIX_STRING)) {
			title = title.substring(1);
		}
		List<ZipEntry> entries = new LinkedList<ZipEntry>();
		int lastSlashPos = title.lastIndexOf(IOUtils.DIR_SEPARATOR_UNIX);
		if (lastSlashPos != -1) {
			String directory = title.substring(0, lastSlashPos);
			String currentDirectory = "";
			List<String> directories = getDirectories();
			for (String part : StringUtils.split(directory, IOUtils.DIR_SEPARATOR_UNIX)) {
				currentDirectory += part + IOUtils.DIR_SEPARATOR_UNIX;
				if (!directories.contains(currentDirectory)) {
					entries.add(new ZipEntry(currentDirectory));
					directories.add(currentDirectory);
				}
			}
		}
		entries.add(new ZipEntry(title));
		return entries;
	}
	
	@Override
	public void closeStream(EncodedTrackBean encodedTrackBean, String title, ZipOutputStream out) throws IOException {
		// Do nothing
	}

	@Override
	public void close() throws IOException {
		getZipOutputStream().close();
	}

	@Override
	public void create() throws IOException {
		// Do nothing
	}

	public ZipOutputStream getZipOutputStream() {
		return i_zipOutputStream;
	}

	public void setZipOutputStream(ZipOutputStream zipOutputStream) {
		i_zipOutputStream = zipOutputStream;
	}

	public List<String> getDirectories() {
		return i_directories;
	}

	public void setDirectories(List<String> directories) {
		i_directories = directories;
	}

}
