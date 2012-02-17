package uk.co.unclealex.music;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.inject.EncodedDirectory;
import uk.co.unclealex.music.inject.Encodings;
import uk.co.unclealex.music.inject.FlacDirectory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.inject.Inject;

public class FileServiceImpl implements FileService {

	private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

	private File i_flacDirectory;
	private File i_encodedDirectory;
	private SortedSet<Encoding> i_encodings;

	@Inject
	protected FileServiceImpl(@FlacDirectory File flacDirectory, @EncodedDirectory File encodedDirectory,
			@Encodings SortedSet<Encoding> encodings) {
		i_flacDirectory = flacDirectory;
		i_encodedDirectory = encodedDirectory;
		i_encodings = encodings;
	}

	@Override
	public String relativiseFile(File file) {
		List<File> fileRoots = new ArrayList<File>();
		SortedSet<Encoding> encodings = getEncodings();
		for (char ch = 'a'; ch <= 'z'; ch++) {
			final char chr = ch;
			Function<Encoding, File> function = new Function<Encoding, File>() {
				@Override
				public File apply(Encoding encoding) {
					return new File(createEncodedRoot(encoding).getPath(), String.valueOf(chr));
				}
			};
			Iterables.addAll(fileRoots, Iterables.transform(encodings, function));
		}
		fileRoots.add(getFlacDirectory());
		return relativiseFile(file, fileRoots);
	}

	@Override
	public String relativiseEncodedFileKeepingFirstLetter(File file) {
		Function<Encoding, File> function = new Function<Encoding, File>() {
			@Override
			public File apply(Encoding encoding) {
				return createEncodedRoot(encoding);
			}
		};
		return relativiseFile(file, Iterables.transform(getEncodings(), function));
	}

	protected String relativiseFile(File file, Iterable<File> fileRoots) {
		String relativePath = null;
		Predicate<File> isParentOfPredicate = new ParentFilePredicate(file);
		for (Iterator<File> iter = fileRoots.iterator(); relativePath == null && iter.hasNext();) {
			File rootFile = iter.next();
			if (isParentOfPredicate.apply(rootFile)) {
				URI parentUri = rootFile.toURI();
				URI childUri = file.toURI();
				URI relativeUri = parentUri.relativize(childUri);
				relativePath = relativeUri.getPath();
			}
		}
		return relativePath;
	}

	protected File createEncodedRoot(Encoding encoding) {
		return new File(getEncodedDirectory(), encoding.getExtension());
	}

	@Override
	public File translateEncodedFileToFlacFile(File encodedFile) {
		String filename = encodedFile.getName();
		String newFilename = renameEncodedToFlac(FilenameUtils.getBaseName(filename)) + "." + Constants.FLAC;
		return new File(translateEncodedDirectoryToFlacDirectory(encodedFile.getParentFile()), newFilename);
	}

	@Override
	public File translateFlacFileToEncodedFile(File flacFile, Encoding encoding) {
		String filename = flacFile.getName();
		String newFilename = renameFlacToEncoded(FilenameUtils.getBaseName(filename)) + "." + encoding.getExtension();
		return new File(translateFlacDirectoryToEncodedDirectory(flacFile.getParentFile(), encoding), newFilename);
	}

	@Override
	public File translateEncodedDirectoryToFlacDirectory(File encodedDirectory) {
		File flacDirectory = getFlacDirectory();
		File parentDirectory = encodedDirectory.getParentFile();
		File rootEncodedDirectory = getEncodedDirectory();
		if (rootEncodedDirectory.equals(parentDirectory) || rootEncodedDirectory.equals(parentDirectory.getParentFile())) {
			return flacDirectory;
		}
		else {
			String relativeDirectoryPath = relativiseFile(encodedDirectory);
			return new File(flacDirectory, renameEncodedToFlac(relativeDirectoryPath));
		}
	}

	@Override
	public File translateFlacDirectoryToEncodedDirectory(File flacDirectory, Encoding encoding) {
		File encodedRoot = createEncodedRoot(encoding);
		if (getFlacDirectory().equals(flacDirectory)) {
			return encodedRoot;
		}
		else {
			String relativeDirectoryPath = relativiseFile(flacDirectory);
			char firstLetterOfArtist = getFirstLetter(relativeDirectoryPath);
			return new File(new File(encodedRoot, String.valueOf(firstLetterOfArtist)),
					renameFlacToEncoded(relativeDirectoryPath));
		}
	}

	@Override
	public char getFirstLetter(String relativePath) {
		int offset = (relativePath.startsWith("the ") || relativePath.startsWith("the_")) ? 4 : 0;
		return relativePath.charAt(offset);
	}

	protected String renameEncodedToFlac(String relativePath) {
		return relativePath.replace(' ', '_');
	}

	protected String renameFlacToEncoded(String relativePath) {
		return relativePath.replace('_', ' ');
	}

	@Override
	public boolean isFlacFileIsOwnedBy(File flacFile, final String owner) {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return ("owner." + owner).equals(name);
			}
		};
		return isFlacFileIsOwnedBy(flacFile.getParentFile(), filter);
	}

	protected boolean isFlacFileIsOwnedBy(File directory, FilenameFilter filter) {
		if (directory == null) {
			return false;
		}
		else {
			return directory.listFiles(filter).length != 0 || isFlacFileIsOwnedBy(directory.getParentFile(), filter);
		}
	}

	@Override
	public SortedSet<File> listFiles(File f, FileFilter fileFilter) {
		TreeSet<File> acceptedFiles = new TreeSet<File>();
		listFiles(f, fileFilter, acceptedFiles);
		return acceptedFiles;
	}

	protected void listFiles(File f, FileFilter fileFilter, SortedSet<File> acceptedFiles) {
		if (f.isDirectory()) {
			File[] children = f.listFiles();
			Arrays.sort(children);
			for (File child : children) {
				listFiles(child, fileFilter, acceptedFiles);
			}
		}
		else {
			if (fileFilter.accept(f)) {
				acceptedFiles.add(f);
			}
		}
	}

	@Override
	public void copy(File source, File target, boolean overwrite) throws IOException {
		doCopy("Copying", source, target, overwrite);
	}

	@Override
	public boolean move(File source, File target, boolean overwrite) throws IOException {
		boolean moved = !source.equals(target);
		if (moved) {
			doCopy("Moving", source, target, overwrite);
			if (!source.delete()) {
				throw new IOException("Cannot delete file " + source.getAbsolutePath());
			}
		}
		return moved;
	}

	protected void doCopy(String message, File source, File target, boolean overwrite) throws IOException {
		String targetPath = target.getAbsolutePath();
		String sourcePath = source.getAbsolutePath();
		log.info(message + " " + sourcePath + " to " + targetPath);
		if (target.exists()) {
			if (overwrite) {
				log.info("Existing target " + targetPath + " will be overwritten.");
			}
			else {
				throw new IOException(message + " " + sourcePath + " failed as target " + targetPath + " exists.");
			}
		}
		target.getParentFile().mkdirs();
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(target);
			in.getChannel().transferTo(0, source.length(), out.getChannel());
		}
		finally {
			Closeables.closeQuietly(in);
			Closeables.closeQuietly(out);
		}
	}

	@Override
	public SortedSet<File> expandOwnedDirectories(SortedSet<File> ownedFlacDirectories, Set<File> flacDirectories) {
		if (ownedFlacDirectories.contains(getFlacDirectory())) {
			Function<File, File> artistDirectoryFunction = new Function<File, File>() {
				@Override
				public File apply(File f) {
					return f.getParentFile();
				}
			};
			ownedFlacDirectories = Sets.newTreeSet(Iterables.transform(flacDirectories, artistDirectoryFunction));
		}
		return ownedFlacDirectories;
	}

	@Override
	public File makeFile(String... names) {
		File f = new File("/");
		for (String name : names) {
			f = new File(f, name);
		}
		return f;
	}

	public File getFlacDirectory() {
		return i_flacDirectory;
	}

	public File getEncodedDirectory() {
		return i_encodedDirectory;
	}

	public SortedSet<Encoding> getEncodings() {
		return i_encodings;
	}
}
