/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author alex
 *
 */
public class FlacIOUtils {

	/**
	 * @param processIn
	 * @return
	 * @throws IOException 
	 */
	public static List<String> readLines(InputStream in) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}

	/**
	 * @param processErr
	 * @return
	 * @throws IOException 
	 */
	public static String toString(InputStream in) throws IOException {
		StringWriter writer = new StringWriter();
		int by;
		while ((by = in.read()) != -1) {
			writer.write(by);
		}
		return writer.toString();
	}

	/**
	 * @param command
	 * @param log TODO
	 * @return
	 * @throws IOException 
	 */
	public static InputStream runCommand(String[] command, Logger log) throws IOException {
		StringBuffer buffer = new StringBuffer();
		for (String cmd : command) {
			buffer.append(cmd).append(' ');
		}
		log.debug(buffer.toString());
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		Process process = builder.start();
		int exitStatus = -1;
		try {
			exitStatus = process.waitFor();
		} catch (InterruptedException e) {
		}
		if (exitStatus != 0) {
			InputStream processErr = process.getErrorStream();
			String message = "Error status " + exitStatus + ": " + FlacIOUtils.toString(processErr);
			processErr.close();
			throw new IOException(message);
		}
		return process.getInputStream();
	}

	public static IterableIterator<File> getAllFiles(File directory, final FilenameFilter filter) {
		return getAllFiles(
				directory,
				new FileFilter() {
					public boolean accept(File file) {
						return filter.accept(file.getParentFile(), file.getName());
					}
				}
		);
	}
	
	public static IterableIterator<File> getAllFiles(File directory, final FileFilter filter) {
		List<File> files = new ArrayList<File>();
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() || filter.accept(pathname);
			}
		};
		getAllFiles(directory, filter, directoryFilter, files);
		return new IterableIterator<File>(files.iterator());
	}
	
	private static void getAllFiles(File directory, FileFilter originalFilter, FileFilter directoryFilter, List<File> allFiles) {
		File[] files = directory.listFiles(directoryFilter);
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				getAllFiles(file, originalFilter, directoryFilter, allFiles);
			}
			if (originalFilter.accept(file)) {
				allFiles.add(file);
			}
		}
	}

	/**
	 * @param baseDirectory
	 * @param extension
	 * @return
	 */
	public static IterableIterator<File> getAllFilesWithExtension(File baseDirectory, String extension) {
		final String end = "." + extension;
	return FlacIOUtils.getAllFiles(
			baseDirectory, 
			new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(end);
				}
			});
	}

	/**
	 * @param title
	 * @param str TODO
	 * @return
	 */
	public static String sanitise(String str) {
		for (char c : FileBasedDAO.INVALID_CHARACTERS) {
			str = str.replace(c, '_');
		}
		return str;
	}

	/**
	 * @param baseDirectory
	 */
	public static void pruneDirectories(File baseDirectory, Logger log) {
		boolean again;
		do {
			again = false;
			FileFilter emptyDirectoryFilter = new FileFilter() {
				public boolean accept(File file) {
					return file.isDirectory() && file.list().length == 0;
				}
			};
			for (File emptyDirectory : FlacIOUtils.getAllFiles(baseDirectory, emptyDirectoryFilter)) {
				log.debug("Deleting " + emptyDirectory.getAbsolutePath());
				deleteFile(emptyDirectory, log);
				again = true;
			}
		} while (again);
	}
	
	public static void deleteFile(File file, Logger log) {
		if (!file.delete()) {
			log.warn("Could not delete " + file.getAbsolutePath());
		}
	}
}