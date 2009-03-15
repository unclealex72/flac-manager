package uk.co.unclealex.music.core.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import uk.co.unclealex.music.base.service.SpeechWriter;

public abstract class AbstractSpeechWriter implements SpeechWriter {

	private static final Logger log = Logger.getLogger(AbstractSpeechWriter.class);
	private static final String LOWER_CASE_ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	private static final String UPPER_CASE_ALPHABET = LOWER_CASE_ALPHABET.toUpperCase();
	private static final String DIGITS = "0123456789";
	private static final String CHARACTERS_TO_KEEP = LOWER_CASE_ALPHABET + UPPER_CASE_ALPHABET + DIGITS;
	private static final String CHARACTERS_TO_IGNORE = "\'";

	private SortedSet<File> i_writtenDirectoriesCache = new TreeSet<File>();
	
	@SuppressWarnings("unchecked")
	@Override
	public void writeSpeechFiles(File baseDirectory, String extension) {
		getWrittenDirectoriesCache().add(baseDirectory);
		Collection<File> files = FileUtils.listFiles(baseDirectory, new String[] { extension }, true);
		for (File file : files) {
			try {
				processFile(file);
			}
			catch (IOException e) {
				log.warn("An error occured creating the speech file for " + file, e);
			}
		}
	}

	protected void processFile(File file) throws IOException {
		File parent = file.getParentFile();
		if (getWrittenDirectoriesCache().add(parent)) {
			processFile(parent);
		}
		File speechFile = createSpeechFile(file);
		if (speechFile.exists()) {
			log.info("Ignoring speech file generation for file " + file);
		}
		else {
			char[] name = FilenameUtils.getBaseName(file.getName()).toCharArray();
			StringBuffer cleanedName = new StringBuffer();
			for (char c : name) {
				if (CHARACTERS_TO_KEEP.indexOf(c) != -1) {
					cleanedName.append(c);
				}
				else if (CHARACTERS_TO_IGNORE.indexOf(c) == -1) {
					cleanedName.append(' ');
				}
			}
			writeSpeechFile(file, cleanedName.toString(), speechFile);
			log.info("Wrote speech for file " + file);
		}
	}
	
	protected abstract File createSpeechFile(File file) throws IOException;
	
	protected abstract void writeSpeechFile(File actualFile, String name, File speechFile) throws IOException;

	public SortedSet<File> getWrittenDirectoriesCache() {
		return i_writtenDirectoriesCache;
	}

}
