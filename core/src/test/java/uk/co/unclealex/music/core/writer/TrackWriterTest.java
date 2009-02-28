package uk.co.unclealex.music.core.writer;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.base.service.titleformat.TitleFormatServiceFactory;
import uk.co.unclealex.music.base.writer.TrackStream;
import uk.co.unclealex.music.base.writer.TrackWriter;
import uk.co.unclealex.music.base.writer.TrackWriterFactory;
import uk.co.unclealex.music.core.CoreSpringTest;
import uk.co.unclealex.music.test.TestTrackStreamImpl;

public class TrackWriterTest extends CoreSpringTest {

	private TrackWriterFactory i_trackWriterFactory;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	
	public void testWrite() throws IOException {
		String titleFormat = "${1:artist}/${artist}/${album}/${2:track} - ${title}.${ext}";
		TitleFormatService titleFormatService = getTitleFormatServiceFactory().createTitleFormatService(titleFormat);
		Map<TrackStream, TitleFormatService> testTrackStreams = new HashMap<TrackStream, TitleFormatService>();
		List<SortedMap<String, Long>> fileNamesAndSizes = new LinkedList<SortedMap<String,Long>>();
		for (int idx = 0; idx < 2; idx++) {
			SortedMap<String, Long> map = new TreeMap<String, Long>();
			fileNamesAndSizes.add(map);
			testTrackStreams.put(new TestTrackStreamImpl(map), titleFormatService);
		}
		
		TrackWriter writer = getTrackWriterFactory().createTrackWriter(testTrackStreams);
		Map<String, Long> expectedFileNamesAndSizes = new TreeMap<String, Long>();
		for (EncodedTrackBean encodedTrackBean : getEncodedTrackDao().getAll()) {
			writer.write(encodedTrackBean);
			expectedFileNamesAndSizes.put(
					titleFormatService.getTitle(encodedTrackBean),
					encodedTrackBean.getTrackDataBean().getFile().length());
		}
		
		int run = 1;
		for (Map<String, Long> actualFileNamesAndSizes : fileNamesAndSizes) {
			assertEquals(
				"The wrong tracks and lengths were returned on run " + run + ".",
				expectedFileNamesAndSizes, actualFileNamesAndSizes);
			run++;
		}
	}
	
	public TrackWriterFactory getTrackWriterFactory() {
		return i_trackWriterFactory;
	}

	@Required
	public void setTrackWriterFactory(TrackWriterFactory trackWriterFactory) {
		i_trackWriterFactory = trackWriterFactory;
	}

	public TitleFormatServiceFactory getTitleFormatServiceFactory() {
		return i_titleFormatServiceFactory;
	}

	@Required
	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}
}
	