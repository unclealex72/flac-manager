package uk.co.unclealex.music.legacy.encoding;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.legacy.Constants;

import com.google.common.base.Predicate;

/**
 * The default implementation of {@link OwnerOrPictureFileService}
 * 
 * @author alex
 * 
 */
public class OwnerOrPictureFileServiceImpl implements OwnerOrPictureFileService {

	private final Logger log = LoggerFactory.getLogger(OwnerOrPictureFileServiceImpl.class);

	private final ImageService i_imageService;

	@Inject
	public OwnerOrPictureFileServiceImpl(ImageService imageService) {
		super();
		i_imageService = imageService;
	}

	/**
	 * Determine whether a file is an owner file, a picture file or neither.
	 * 
	 * @param file
	 *          The file to check
	 * @return True, if the file is an owner file or picture file, false
	 *         otherwise.
	 */
	protected boolean isOwnerOrPictureFile(File file) {
		try {
			return file.isFile()
					&& (Constants.OWNER.equals(FilenameUtils.getBaseName(file.getName())) || getImageService().loadImage(file) != null);
		}
		catch (IOException e) {
			log.warn("Could not determine whether file " + file + " is an image file. Assuming it isn't.");
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileFilter asFileFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File file) {
				return isOwnerOrPictureFile(file);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Predicate<File> asPredicate() {
		return new Predicate<File>() {
			@Override
			public boolean apply(File file) {
				return isOwnerOrPictureFile(file);
			}
		};
	}

	public ImageService getImageService() {
		return i_imageService;
	}

}
