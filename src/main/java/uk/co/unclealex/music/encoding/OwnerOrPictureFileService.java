package uk.co.unclealex.music.encoding;

import java.io.File;
import java.io.FileFilter;

import com.google.common.base.Predicate;

/**
 * A small service that can be used to determine whether a given file is either an owner file or a picture file.
 * @author alex
 *
 */
public interface OwnerOrPictureFileService {

	/**
	 * Masquerade this service as a {@link FileFilter}
	 * 
	 * @return A {@link FileFilter} that returns true if, and only if, a given file is either an owner file or a picture file.
	 */
	public FileFilter asFileFilter();
	
	/**
	 * Masquerade this service as a {@link Predicate}
	 * 
	 * @return A {@link Predicate} that returns true if, and only if, a given file is either an owner file or a picture file.
	 */
	public Predicate<File> asPredicate();
}
