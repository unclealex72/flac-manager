package uk.co.unclealex.music.legacy.sync;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class DynamicMountPointFinder implements MountPointFinder {

	private final File i_parentFile;
	private final String i_fileNameToFind;
	
	public DynamicMountPointFinder(File parentFile, String fileNameToFind) {
		super();
		i_parentFile = parentFile;
		i_fileNameToFind = fileNameToFind;
	}

	@Override
	public File findMountPoint() {
		FileFilter directoryFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		File[] mountPoints = getParentFile().listFiles(directoryFilter);
		final String fileNameToFind = getFileNameToFind();
		class MountPointFinder implements FilenameFilter, Predicate<File> {
			@Override
			public boolean accept(File dir, String name) {
				return name.equals(fileNameToFind);
			}
			@Override
			public boolean apply(File directory) {
				return directory.isDirectory() && directory.listFiles(this).length != 0;
			}
		}
		return Iterables.find(Arrays.asList(mountPoints), new MountPointFinder(), null);
	}

	public File getParentFile() {
		return i_parentFile;
	}

	public String getFileNameToFind() {
		return i_fileNameToFind;
	}

}
