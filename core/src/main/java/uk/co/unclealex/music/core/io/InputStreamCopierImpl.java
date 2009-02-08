package uk.co.unclealex.music.core.io;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class InputStreamCopierImpl implements InputStreamCopier {

	@Override
	public void copy(DataExtractor extractor, int id, final KnownLengthOutputStream<?> out) throws IOException {
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
		extractor.extractData(id, callback);
	}

	public void copy(DataExtractor extractor, int id, OutputStream out) throws IOException {
		KnownLengthOutputStream<OutputStream> kOut = new KnownLengthOutputStream<OutputStream>(out) {
			@Override
			protected void setLength(int length) throws IOException {
				// Do nothing
			}
		};
		copy(extractor, id, kOut);
	}
}
