package uk.co.unclealex.music.core.service;

import java.sql.Blob;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.io.KnownLengthInputStream;

@Service("blobFactory")
@Transactional
public class HibernateBlobFactory implements BlobFactory {

	@Override
	public Blob createBlob(KnownLengthInputStream stream) {
		return Hibernate.createBlob(stream, stream.getLength());
	}

}
