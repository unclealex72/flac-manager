package uk.co.unclealex.music.test;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.io.output.NullOutputStream;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.model.EncodedTrackBean;

@Transactional
public class TestTrackStreamImpl implements TestTrackStream {

	private Map<String, Long> i_fileNamesAndSizes;
	private String i_title;
	private CountingKnownLengthOutputStream i_outputStream;
	
	public TestTrackStreamImpl(SortedMap<String, Long> fileNamesAndSizes) {
		i_fileNamesAndSizes = fileNamesAndSizes;
	}

	@Override
	public CountingKnownLengthOutputStream createStream(EncodedTrackBean encodedTrackBean,
			String title) throws IOException {
		setTitle(title);
		setOutputStream(new CountingKnownLengthOutputStream(new NullOutputStream(), new NullWritableByteChannel()));
		return getOutputStream();
	}

	@Override
	public void closeStream() throws IOException {
		CountingKnownLengthOutputStream outputStream = getOutputStream();
		outputStream.flush();
		getFileNamesAndSizes().put(getTitle(), (long) getOutputStream().getCount());
		setOutputStream(null);
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void create() throws IOException {
	}

	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.base.core.encoded.writer.TestTrackWriter#getFileNamesAndSizes()
	 */
	public Map<String, Long> getFileNamesAndSizes() {
		return i_fileNamesAndSizes;
	}

	public void setFileNamesAndSizes(Map<String, Long> fileNamesAndSizes) {
		i_fileNamesAndSizes = fileNamesAndSizes;
	}

	public String getTitle() {
		return i_title;
	}

	public void setTitle(String title) {
		i_title = title;
	}

	public CountingKnownLengthOutputStream getOutputStream() {
		return i_outputStream;
	}

	public void setOutputStream(CountingKnownLengthOutputStream outputStream) {
		i_outputStream = outputStream;
	}

}
