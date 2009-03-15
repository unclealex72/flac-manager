package uk.co.unclealex.music.core.io;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.io.DataExtractor;
import uk.co.unclealex.music.base.io.InputStreamCopier;
import uk.co.unclealex.music.base.io.KnownLengthInputStream;
import uk.co.unclealex.music.base.io.KnownLengthInputStreamCallback;
import uk.co.unclealex.music.base.io.KnownLengthOutputStream;

@Transactional
@Service("inputStreamCopier")
public class InputStreamCopierImpl<E> implements InputStreamCopier<E> {

	@Override
	public void copy(DataExtractor<E> extractor, E element, final KnownLengthOutputStream out) throws IOException {
		KnownLengthInputStreamCallback callback = createCallback(out);
		extractor.extractData(element, callback);
	}

	@Override
	public void copy(DataExtractor<E> extractor, int id, KnownLengthOutputStream out) throws IOException {
		KnownLengthInputStreamCallback callback = createCallback(out);
		extractor.extractData(id, callback);
	}
	
	protected KnownLengthInputStreamCallback createCallback(final KnownLengthOutputStream out) {
		KnownLengthInputStreamCallback callback = new KnownLengthInputStreamCallback() {
			@Override
			public void execute(KnownLengthInputStream in) throws IOException {
				try {
					boolean written = false;
					out.setLength(in.getLength());
					if (in.hasChannel() && out.hasChannel()) {
						ReadableByteChannel readableByteChannel = in.getChannel();
						WritableByteChannel writableByteChannel = out.getChannel();
						if (readableByteChannel instanceof FileChannel) {
							written = true;
							((FileChannel) readableByteChannel).transferTo(0, in.getLength(), writableByteChannel);
						}
						else if (writableByteChannel instanceof FileChannel) {
							written = true;
							((FileChannel) writableByteChannel).transferFrom(readableByteChannel, 0, in.getLength());
						}
					}
					if (!written) {
						IOUtils.copy(in, out);
					}
				}
				finally {
					IOUtils.closeQuietly(in);
				}
			}
		};
		return callback;
	}
}
