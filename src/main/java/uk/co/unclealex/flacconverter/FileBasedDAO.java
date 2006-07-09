/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * @author alex
 *
 */
public class FileBasedDAO implements FormatDAO {

	private File i_baseDirectory;
	private FileCodec i_fileCodec;
	public static char[] INVALID_CHARACTERS = "/\\:".toCharArray();
	
	/**
	 * @param baseDirectory
	 * @param fileCodec
	 */
	public FileBasedDAO(File baseDirectory, FileCodec fileCodec) {
		super();
		i_baseDirectory = baseDirectory;
		i_fileCodec = fileCodec;
	}

	public IterableIterator<Track> findAllTracks(final Logger log) {
		final Iterator<File> foundFiles = IOUtils.getAllFilesWithExtension(getBaseDirectory(), getExtension());
		Iterator<Track> iterator = new Iterator<Track>() {

			public boolean hasNext() {
				return foundFiles.hasNext();
			}

			public Track next() {
				File file = foundFiles.next();
				try {
					return loadTrack(file, log);
				} catch (IOException e) {
					return new Track(file, e);
				}
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
		return new IterableIterator<Track>(iterator);
	}

	protected Track loadTrack(File file, Logger log) throws IOException {
		InputStream in = IOUtils.runCommand(getFileCodec().generateTagCommand(file), log);
		List<String> output = IOUtils.readLines(in);
		in.close();
		return getFileCodec().processTagCommandOutput(file, output);
	}
	
	/**
	 * @return Returns the baseDirectory.
	 */
	public File getBaseDirectory() {
		return i_baseDirectory;
	}

	/**
	 * @return the fileCodec
	 */
	public FileCodec getFileCodec() {
		return i_fileCodec;
	}

	/* (non-Javadoc)
	 * @see uk.co.unclealex.flacconverter.FormatManager#getExtension()
	 */
	public String getExtension() {
		return getFileCodec().getExtension();
	}
}
