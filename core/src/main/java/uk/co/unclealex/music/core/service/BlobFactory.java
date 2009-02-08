package uk.co.unclealex.music.core.service;

import java.sql.Blob;

import uk.co.unclealex.music.core.io.KnownLengthInputStream;

public interface BlobFactory {

	public Blob createBlob(KnownLengthInputStream stream);
}
