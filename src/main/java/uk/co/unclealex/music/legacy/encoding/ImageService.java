package uk.co.unclealex.music.legacy.encoding;

import java.io.File;
import java.io.IOException;

public interface ImageService {

	public byte[] loadImage(File imageFile) throws IOException;

}
