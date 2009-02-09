package uk.co.unclealex.music.base.writer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TrackWritingException extends Exception {

	private Map<TrackStream, Collection<IOException>> i_ioExceptionsByTrackStream = 
		new HashMap<TrackStream, Collection<IOException>>();
	
	public boolean requiresThrowing() {
		return !getIoExceptionsByTrackStream().isEmpty();
	}

	@Override
	public String getMessage() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		for (Map.Entry<TrackStream, Collection<IOException>> entry : getIoExceptionsByTrackStream().entrySet()) {
			TrackStream trackStream = entry.getKey();
			for (IOException ex : entry.getValue()) {
				out.println(trackStream);
				out.println(ex.getMessage());
				ex.printStackTrace(out);
			}
		}
		return writer.toString();
	}
	
	public void registerExceptions(TrackWritingException trackWritingException) {
		for (
			Map.Entry<TrackStream, Collection<IOException>> entry : 
			trackWritingException.getIoExceptionsByTrackStream().entrySet()) {
			registerExceptions(entry.getKey(), entry.getValue());
		}
	}
	
	public void registerExceptions(TrackStream trackStream, Collection<IOException> ioExceptions) {
		for (IOException ioException :  ioExceptions) {
			registerException(trackStream, ioException);
		}
	}

	public void registerException(TrackStream trackStream, IOException ioException) {
		Map<TrackStream, Collection<IOException>> ioExceptionsByTrackStream = getIoExceptionsByTrackStream();
		Collection<IOException> ioExceptions = ioExceptionsByTrackStream.get(trackStream);
		if (ioExceptions == null) {
			ioExceptions = new LinkedList<IOException>();
			ioExceptionsByTrackStream.put(trackStream, ioExceptions);
		}
		ioExceptions.add(ioException);
	}

	public Map<TrackStream, Collection<IOException>> getIoExceptionsByTrackStream() {
		return i_ioExceptionsByTrackStream;
	}
}
