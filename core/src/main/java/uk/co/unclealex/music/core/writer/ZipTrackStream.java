package uk.co.unclealex.music.core.writer;

import java.util.zip.ZipOutputStream;

public interface ZipTrackStream extends TrackStream {

	public void setZipOutputStream(ZipOutputStream zipOutputStream);

}