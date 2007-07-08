package uk.co.unclealex.flacconverter.encoded.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;

public class FileTrackWriter extends AbstractTrackWriter<FileOutputStream> {

	private File i_rootDirectory;
	
	@Override
	public FileOutputStream createStream(EncodedTrackBean encodedTrackBean, String title) throws IOException {
		File f = new File(getRootDirectory(), "");
		for (String part : StringUtils.split(title, File.pathSeparatorChar)) {
			f = new File(f, part);
		}
		md(f.getParentFile());
		return new FileOutputStream(f);
	}

	@Override
	public void closeStream(EncodedTrackBean encodedTrackBean, String title, FileOutputStream out) throws IOException {
		out.close();
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void create() throws IOException {
		md(getRootDirectory());
	}

	protected void md(File directory) throws IOException {
		if (!directory.mkdirs()) {
			throw new IOException("Creating directory " + directory.getCanonicalPath() + " failed.");
		}		
	}
	
	public File getRootDirectory() {
		return i_rootDirectory;
	}

	public void setRootDirectory(File rootDirectory) {
		i_rootDirectory = rootDirectory;
	}

}
