package uk.co.unclealex.music.core.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.ObjectUtils;

import uk.co.unclealex.music.base.io.KnownLengthOutputStream;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.writer.FileTrackStream;
import uk.co.unclealex.spring.Prototype;

@Prototype
public class FileTrackStreamImpl implements FileTrackStream {

	private File i_rootDirectory;
	private KnownLengthOutputStream i_outputStream;
	
	@Override
	public KnownLengthOutputStream createStream(EncodedTrackBean encodedTrackBean, String title) throws IOException {
		File f = new File(getRootDirectory(), title);
		md(f.getParentFile());
		KnownLengthOutputStream out;
		long encodingTime = encodedTrackBean.getTimestamp();
		long lastModifiedTime = f.lastModified();
		if (encodingTime > lastModifiedTime) {
			FileOutputStream fileOutputStream = new FileOutputStream(f);
			out = new KnownLengthOutputStream(fileOutputStream, fileOutputStream.getChannel()) {
				@Override
				public void setLength(int length) throws IOException {
					// Ignore
				}
			};
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
		return ObjectUtils.hashCode(getRootDirectory());
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

	public void setOutputStream(KnownLengthOutputStream outputStream) {
		i_outputStream = outputStream;
	}

}
