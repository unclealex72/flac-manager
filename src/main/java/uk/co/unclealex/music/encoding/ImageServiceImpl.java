package uk.co.unclealex.music.encoding;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

public class ImageServiceImpl implements ImageService {

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
			IOUtils.copy(in, out);
		}
		finally {
			IOUtils.closeQuietly(in);
		}
		return out.toByteArray();
	}

}
