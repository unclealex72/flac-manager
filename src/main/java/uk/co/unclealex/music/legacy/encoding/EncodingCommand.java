package uk.co.unclealex.music.legacy.encoding;

import java.io.File;

import uk.co.unclealex.music.legacy.Encoding;

public class EncodingCommand {

	private Encoding i_encoding;
	private File i_flacFile;
	private File i_destinationFile;
	private File i_encodingScriptFile;
	
	public EncodingCommand(Encoding encoding, File flacFile, File destinationFile, File encodingScriptFile) {
		super();
		i_encoding = encoding;
		i_flacFile = flacFile;
		i_destinationFile = destinationFile;
		i_encodingScriptFile = encodingScriptFile;
	}

	public File getFlacFile() {
		return i_flacFile;
	}

	public File getDestinationFile() {
		return i_destinationFile;
	}

	public File getEncodingScriptFile() {
		return i_encodingScriptFile;
	}

	public Encoding getEncoding() {
		return i_encoding;
	}
}
