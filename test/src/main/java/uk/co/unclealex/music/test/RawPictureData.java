package uk.co.unclealex.music.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RawPictureData {

	private byte[] i_data;
	private BufferedImage i_bufferedImage;
	
	public RawPictureData(byte[] data) throws IOException {
		super();
		i_data = data;
		i_bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		BufferedImage bufferedImage = getBufferedImage();
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		builder.append(width).append('*').append(height).append(':');
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (x != 0 || y != 0) {
					builder.append(',');
				}
				builder.append(bufferedImage.getRGB(x, y));
			}
		}
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof RawPictureData && toString().equals(obj.toString());
	}
	
	public byte[] getData() {
		return i_data;
	}
	
	public BufferedImage getBufferedImage() {
		return i_bufferedImage;
	}
}
