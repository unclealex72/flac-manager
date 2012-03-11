package uk.co.unclealex.music.legacy;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;


public interface FileService {

	public File translateFlacFileToEncodedFile(File flacFile, Encoding encoding);

	public File translateEncodedFileToFlacFile(File encodedFile);

	public File translateFlacDirectoryToEncodedDirectory(File flacDirectory, Encoding encoding);

	public String relativiseFile(File file);

	public File translateEncodedDirectoryToFlacDirectory(File encodedDirectory);

	public SortedSet<File> listFiles(File f, FileFilter fileFilter);

	public char getFirstLetter(String relativePath);

	public void copy(File source, File target, boolean overwrite) throws IOException;
	
	public boolean move(File source, File target, boolean overwrite) throws IOException;

	public SortedSet<File> expandOwnedDirectories(SortedSet<File> ownedFlacDirectories, Set<File> flacDirectories);

	public boolean isFlacFileIsOwnedBy(File flacFile, String owner);
	
	public File makeFile(String... names);

	public String relativiseEncodedFileKeepingFirstLetter(File file);
}
