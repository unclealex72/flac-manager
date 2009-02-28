package uk.co.unclealex.music.core.io;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.io.DataExtractor;
import uk.co.unclealex.music.base.io.InputStreamCopier;
import uk.co.unclealex.music.base.io.KnownLengthInputStream;
import uk.co.unclealex.music.base.io.KnownLengthInputStreamCallback;
import uk.co.unclealex.music.base.io.KnownLengthOutputStream;

@Transactional
@Service
public class InputStreamCopierImpl<E> implements InputStreamCopier<E>, Transformer<OutputStream, KnownLengthOutputStream<?>> {

	@Override
	public void copy(DataExtractor<E> extractor, E element, final KnownLengthOutputStream<?> out) throws IOException {
		KnownLengthInputStreamCallback callback = createCallback(out);
		extractor.extractData(element, callback);
	}

	public void copy(DataExtractor<E> extractor, E element, OutputStream out) throws IOException {
		copy(extractor, element, transform(out));
	}
	
	@Override
	public void copy(DataExtractor<E> extractor, int id, KnownLengthOutputStream<?> out) throws IOException {
		KnownLengthInputStreamCallback callback = createCallback(out);
		extractor.extractData(id, callback);
	}
	
	@Override
	public void copy(DataExtractor<E> extractor, int id, OutputStream out) throws IOException {
		copy(extractor, id, transform(out));
	}
	
	protected KnownLengthInputStreamCallback createCallback(final KnownLengthOutputStream<?> out) {
		KnownLengthInputStreamCallback callback = new KnownLengthInputStreamCallback() {
			@Override
			public void execute(KnownLengthInputStream in) throws IOException {
				try {
					out.setLength(in.getLength());
					IOUtils.copy(in, out);
				}
				finally {
					IOUtils.closeQuietly(in);
				}
			}
		};
		return callback;
	}

	@Override
	public KnownLengthOutputStream<?> transform(final OutputStream out) {
		KnownLengthOutputStream<OutputStream> kOut = new KnownLengthOutputStream<OutputStream>(out) {
			@Override
			public void setLength(int length) throws IOException {
				// Do nothing
			}
		};
		return kOut;
	}
}
