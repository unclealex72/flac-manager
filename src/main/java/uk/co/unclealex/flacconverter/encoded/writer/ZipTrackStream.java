package uk.co.unclealex.flacconverter.encoded.writer;

import java.util.zip.ZipOutputStream;

public interface ZipTrackStream extends TrackStream {

	public void setZipOutputStream(ZipOutputStream zipOutputStream);

}