package uk.co.unclealex.music.base.dao;

import java.io.IOException;

import org.hibernate.Session;

import uk.co.unclealex.music.base.io.KnownLengthInputStreamCallback;

public interface Streamer {

	public abstract void stream(Session session, String fieldName,
			String entityName, int id, KnownLengthInputStreamCallback callback)
			throws IOException;

}