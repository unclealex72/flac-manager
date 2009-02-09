package uk.co.unclealex.music.core.writer;

import java.util.zip.ZipOutputStream;

import uk.co.unclealex.music.base.writer.TrackStream;

public interface ZipTrackStream extends TrackStream {

	public void setZipOutputStream(ZipOutputStream zipOutputStream);

}