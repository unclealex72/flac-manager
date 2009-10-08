package uk.co.unclealex.music.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.commons.io.IOUtils;

public class FileHandle {

	private File i_file;
	private FileInputStream i_fileInputStream;
	private FileChannel i_fileChannel;
	
	public FileHandle(File file) {
		super();
		setFile(file);
	}
	
	protected void initialise() throws IOException {
		FileInputStream fileInputStream = new FileInputStream(getFile());
		FileChannel fileChannel = fileInputStream.getChannel();
		setFileInputStream(fileInputStream);
		setFileChannel(fileChannel);
	}

	public void closeQuietly() {
		IOUtils.closeQuietly(getFileInputStream());
	}
	
	protected File getFile() {
		return i_file;
	}

	protected void setFile(File file) {
		i_file = file;
	}

	public FileChannel getFileChannel() throws IOException {
		if (i_fileChannel == null) {
			initialise();
		}
		return i_fileChannel;
	}

	protected void setFileChannel(FileChannel fileChannel) {
		i_fileChannel = fileChannel;
	}

	protected FileInputStream getFileInputStream() {
		return i_fileInputStream;
	}

	protected void setFileInputStream(FileInputStream fileInputStream) {
		i_fileInputStream = fileInputStream;
	}
}
