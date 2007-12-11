package uk.co.unclealex.music.core.encoded.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import uk.co.unclealex.music.core.encoded.model.EncodedTrackBean;

public class FileTrackStreamImpl implements FileTrackStream {

	private File i_rootDirectory;
	private OutputStream i_outputStream;
	
	@Override
	public OutputStream createStream(EncodedTrackBean encodedTrackBean, String title) throws IOException {
		File f = new File(getRootDirectory(), title);
		md(f.getParentFile());
		OutputStream out;
		long encodingTime = encodedTrackBean.getTimestamp();
		long lastModifiedTime = f.lastModified();
		if (encodingTime > lastModifiedTime) {
			out = new FileOutputStream(f);
		}
		else {
			out = null;
		}
		setOutputStream(out);
		return out;
	}

	@Override
	public void closeStream() throws IOException {
		getOutputStream().close();
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void create() throws IOException {
		md(getRootDirectory());
	}

	protected void md(File directory) throws IOException {
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Creating directory " + directory.getCanonicalPath() + " failed.");
		}		
	}

	@Override
	public String toString() {
		return getRootDirectory().getPath();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof FileTrackStreamImpl && 
			((FileTrackStreamImpl) obj).getRootDirectory().equals(getRootDirectory());
	}
	
	@Override
	public int hashCode() {
		return getRootDirectory().hashCode();
	}
	
	public File getRootDirectory() {
		return i_rootDirectory;
	}

	public void setRootDirectory(File rootDirectory) {
		i_rootDirectory = rootDirectory;
	}

	public OutputStream getOutputStream() {
		return i_outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		i_outputStream = outputStream;
	}

}