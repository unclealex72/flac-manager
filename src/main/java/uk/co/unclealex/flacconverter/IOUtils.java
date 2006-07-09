/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author alex
 *
 */
public class IOUtils {

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
			String message = "Error status " + exitStatus + ": " + IOUtils.toString(processErr);
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
	return IOUtils.getAllFiles(
			baseDirectory, 
			new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(end);
				}
			});
	}

	/**
	 * @param in
	 * @param out
	 */
	public static void copy(InputStream in, OutputStream out) throws IOException {
		BufferedInputStream bIn = new BufferedInputStream(in);
		BufferedOutputStream bOut = new BufferedOutputStream(out);
		int by;
		while ((by = bIn.read()) != -1) {
			bOut.write(by);
		}
	}
}
