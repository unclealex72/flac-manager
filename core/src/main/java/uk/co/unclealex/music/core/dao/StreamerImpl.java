package uk.co.unclealex.music.core.dao;

import java.io.IOException;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.io.KnownLengthInputStream;
import uk.co.unclealex.music.core.io.KnownLengthInputStreamCallback;

@Service
@Transactional
public class StreamerImpl implements Streamer {

	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.core.dao.Streamer#stream(org.hibernate.Session, java.lang.String, java.lang.String, int, uk.co.unclealex.music.core.io.KnownLengthInputStreamCallback)
	 */
	public void stream(Session session, String fieldName, String entityName, int id, KnownLengthInputStreamCallback callback ) throws IOException {
		Query query =
			session.createQuery("select " + fieldName + " from " + entityName + " where id = :id").
			setInteger("id", id);
		ScrollableResults scrollableResults = query.scroll();
		scrollableResults.first();
		KnownLengthInputStream in = (KnownLengthInputStream) scrollableResults.get(0);
		callback.execute(in);
		scrollableResults.close();
		
	}
}
