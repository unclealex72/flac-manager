package uk.co.unclealex.music.test;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.model.EncodedTrackBean;

@Transactional
public class TestTrackStreamImpl implements TestTrackStream {

	private Map<String, Integer> i_fileNamesAndSizes;
	private String i_title;
	private CountingOutputStream i_outputStream;
	
	public TestTrackStreamImpl(SortedMap<String, Integer> fileNamesAndSizes) {
		i_fileNamesAndSizes = fileNamesAndSizes;
	}

	@Override
	public CountingOutputStream createStream(EncodedTrackBean encodedTrackBean,
			String title) throws IOException {
		setTitle(title);
		setOutputStream(new CountingOutputStream(new NullOutputStream()));
		return getOutputStream();
	}

	@Override
	public void closeStream() throws IOException {
		CountingOutputStream outputStream = getOutputStream();
		outputStream.flush();
		getFileNamesAndSizes().put(getTitle(), getOutputStream().getCount());
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
	public Map<String, Integer> getFileNamesAndSizes() {
		return i_fileNamesAndSizes;
	}

	public void setFileNamesAndSizes(Map<String, Integer> fileNamesAndSizes) {
		i_fileNamesAndSizes = fileNamesAndSizes;
	}

	public String getTitle() {
		return i_title;
	}

	public void setTitle(String title) {
		i_title = title;
	}

	public CountingOutputStream getOutputStream() {
		return i_outputStream;
	}

	public void setOutputStream(CountingOutputStream outputStream) {
		i_outputStream = outputStream;
	}

}
