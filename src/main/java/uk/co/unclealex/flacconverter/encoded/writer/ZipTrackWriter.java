package uk.co.unclealex.flacconverter.encoded.writer;

import java.util.zip.ZipOutputStream;

public interface ZipTrackWriter extends TrackWriter {

	public void setZipOutputStream(ZipOutputStream zipOutputStream);

}