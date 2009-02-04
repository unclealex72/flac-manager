package uk.co.unclealex.music.albumcover.service;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Locale;

import javax.imageio.IIOParamController;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;

public class CompressionRevealingImageWriteParam extends ImageWriteParam {

	private ImageWriteParam i_delegate;

	public CompressionRevealingImageWriteParam(ImageWriteParam delegate) {
		super();
		i_delegate = delegate;
	}

	public boolean activateController() {
		return getDelegate().activateController();
	}

	public boolean canOffsetTiles() {
		return getDelegate().canOffsetTiles();
	}

	public boolean canWriteCompressed() {
		return getDelegate().canWriteCompressed();
	}

	public boolean canWriteProgressive() {
		return getDelegate().canWriteProgressive();
	}

	public boolean canWriteTiles() {
		return getDelegate().canWriteTiles();
	}

	public boolean equals(Object obj) {
		return getDelegate().equals(obj);
	}

	public float getBitRate(float quality) {
		return getDelegate().getBitRate(quality);
	}

	public int getCompressionMode() {
		return getDelegate().getCompressionMode();
	}

	public float getCompressionQuality() {
		return getDelegate().getCompressionQuality();
	}

	public String[] getCompressionQualityDescriptions() {
		return getDelegate().getCompressionQualityDescriptions();
	}

	public float[] getCompressionQualityValues() {
		return getDelegate().getCompressionQualityValues();
	}

	public String getCompressionType() {
		return getDelegate().getCompressionType();
	}

	public String[] getCompressionTypes() {
		return getDelegate().getCompressionTypes();
	}

	public IIOParamController getController() {
		return getDelegate().getController();
	}

	public IIOParamController getDefaultController() {
		return getDelegate().getDefaultController();
	}

	public Point getDestinationOffset() {
		return getDelegate().getDestinationOffset();
	}

	public ImageTypeSpecifier getDestinationType() {
		return getDelegate().getDestinationType();
	}

	public Locale getLocale() {
		return getDelegate().getLocale();
	}

	public String getLocalizedCompressionTypeName() {
		return getDelegate().getLocalizedCompressionTypeName();
	}

	public Dimension[] getPreferredTileSizes() {
		return getDelegate().getPreferredTileSizes();
	}

	public int getProgressiveMode() {
		return getDelegate().getProgressiveMode();
	}

	public int[] getSourceBands() {
		return getDelegate().getSourceBands();
	}

	public Rectangle getSourceRegion() {
		return getDelegate().getSourceRegion();
	}

	public int getSourceXSubsampling() {
		return getDelegate().getSourceXSubsampling();
	}

	public int getSourceYSubsampling() {
		return getDelegate().getSourceYSubsampling();
	}

	public int getSubsamplingXOffset() {
		return getDelegate().getSubsamplingXOffset();
	}

	public int getSubsamplingYOffset() {
		return getDelegate().getSubsamplingYOffset();
	}

	public int getTileGridXOffset() {
		return getDelegate().getTileGridXOffset();
	}

	public int getTileGridYOffset() {
		return getDelegate().getTileGridYOffset();
	}

	public int getTileHeight() {
		return getDelegate().getTileHeight();
	}

	public int getTileWidth() {
		return getDelegate().getTileWidth();
	}

	public int getTilingMode() {
		return getDelegate().getTilingMode();
	}

	public boolean hasController() {
		return getDelegate().hasController();
	}

	public int hashCode() {
		return getDelegate().hashCode();
	}

	public boolean isCompressionLossless() {
		return getDelegate().isCompressionLossless();
	}

	public void setCompressionMode(int mode) {
		getDelegate().setCompressionMode(mode);
	}

	public void setCompressionQuality(float quality) {
		getDelegate().setCompressionQuality(quality);
	}

	public void setCompressionType(String compressionType) {
		getDelegate().setCompressionType(compressionType);
	}

	public void setController(IIOParamController controller) {
		getDelegate().setController(controller);
	}

	public void setDestinationOffset(Point destinationOffset) {
		getDelegate().setDestinationOffset(destinationOffset);
	}

	public void setDestinationType(ImageTypeSpecifier destinationType) {
		getDelegate().setDestinationType(destinationType);
	}

	public void setProgressiveMode(int mode) {
		getDelegate().setProgressiveMode(mode);
	}

	public void setSourceBands(int[] sourceBands) {
		getDelegate().setSourceBands(sourceBands);
	}

	public void setSourceRegion(Rectangle sourceRegion) {
		getDelegate().setSourceRegion(sourceRegion);
	}

	public void setSourceSubsampling(int sourceXSubsampling,
			int sourceYSubsampling, int subsamplingXOffset,
			int subsamplingYOffset) {
		getDelegate().setSourceSubsampling(sourceXSubsampling, sourceYSubsampling,
				subsamplingXOffset, subsamplingYOffset);
	}

	public void setTiling(int tileWidth, int tileHeight, int tileGridXOffset,
			int tileGridYOffset) {
		getDelegate().setTiling(tileWidth, tileHeight, tileGridXOffset,
				tileGridYOffset);
	}

	public void setTilingMode(int mode) {
		getDelegate().setTilingMode(mode);
	}

	public String toString() {
		return getDelegate().toString();
	}

	public void unsetCompression() {
		getDelegate().unsetCompression();
	}

	public void unsetTiling() {
		getDelegate().unsetTiling();
	}

	public ImageWriteParam getDelegate() {
		return i_delegate;
	}
}
