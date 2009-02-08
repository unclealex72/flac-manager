package uk.co.unclealex.music.encoder.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DeleteOnClosingFileInputStream extends FileInputStream {

	private File i_file;

	
	
	public DeleteOnClosingFileInputStream(File file) throws FileNotFoundException {
		super(file);
		i_file = file;
	}

	@Override
	public void close() throws IOException {
		super.close();
		getFile().delete();
	}
	
	protected File getFile() {
		return i_file;
	}

}
