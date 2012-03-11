package uk.co.unclealex.music.legacy.covers;

import java.io.File;
import java.io.IOException;

public interface ArtworkManager {

	public boolean artworkExists(File audioFile) throws IOException;
	
	public byte[] getArtwork(File audioFile) throws IOException;

	public void setArtwork(byte[] artwork, File... audioFile) throws IOException;

}
