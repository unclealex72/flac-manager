package uk.co.unclealex.music.legacy.encoding;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.inject.Inject;

public class ImageServiceImpl implements ImageService {

	@Inject
	protected ImageServiceImpl() {
		super();
	}
	
	@Override
	public byte[] loadImage(File imageFile) throws IOException {
		BufferedImage img = ImageIO.read(imageFile);
		if (img != null) {
			return toByteArray(new FileInputStream(imageFile));
		}
		else {
			return null;
		}
	}

	protected byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ByteStreams.copy(in, out);
		}
		finally {
			Closeables.closeQuietly(in);
		}
		return out.toByteArray();
	}

}
