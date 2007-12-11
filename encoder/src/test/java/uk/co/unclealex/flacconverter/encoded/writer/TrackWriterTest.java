package uk.co.unclealex.flacconverter.encoded.writer;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import uk.co.unclealex.flacconverter.EncodedSpringTest;
import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.service.AlreadyEncodingException;
import uk.co.unclealex.flacconverter.encoded.service.CurrentlyScanningException;
import uk.co.unclealex.flacconverter.encoded.service.EncoderService;
import uk.co.unclealex.flacconverter.encoded.service.MultipleEncodingException;
import uk.co.unclealex.flacconverter.encoded.service.titleformat.TitleFormatService;
import uk.co.unclealex.flacconverter.encoded.service.titleformat.TitleFormatServiceFactory;

public class TrackWriterTest extends EncodedSpringTest {

	private EncoderService i_encoderService;
	private TrackWriterFactory i_trackWriterFactory;
	private TitleFormatServiceFactory i_titleFormatServiceFactory;
	private EncodedTrackDao i_encodedTrackDao;
	
	public void testWrite() throws IOException, AlreadyEncodingException, CurrentlyScanningException {
		try {
			getEncoderService().encodeAll(4);
		}
		catch (MultipleEncodingException e) {
			// Ignore.
		}
		String titleFormat = "${1:artist}/${artist}/${album}/${2:track} - ${title}.${ext}";
		TitleFormatService titleFormatService = getTitleFormatServiceFactory().createTitleFormatService(titleFormat);
		Map<TrackStream, TitleFormatService> testTrackStreams = new HashMap<TrackStream, TitleFormatService>();
		List<SortedMap<String, Integer>> fileNamesAndSizes = new LinkedList<SortedMap<String,Integer>>();
		for (int idx = 0; idx < 2; idx++) {
			SortedMap<String, Integer> map = new TreeMap<String, Integer>();
			fileNamesAndSizes.add(map);
			testTrackStreams.put(new TestTrackStreamImpl(map), titleFormatService);
		}
		
		TrackWriter writer = getTrackWriterFactory().createTrackWriter(testTrackStreams);
		Map<String, Integer> expectedFileNamesAndSizes = new TreeMap<String, Integer>();
		for (EncodedTrackBean encodedTrackBean : getEncodedTrackDao().getAll()) {
			writer.write(encodedTrackBean);
			expectedFileNamesAndSizes.put(
					titleFormatService.getTitle(encodedTrackBean),
					encodedTrackBean.getLength());
		}
		
		int run = 1;
		for (Map<String, Integer> actualFileNamesAndSizes : fileNamesAndSizes) {
			assertEquals(
				"The wrong tracks and lengths were returned on run " + run + ".",
				expectedFileNamesAndSizes, actualFileNamesAndSizes);
			run++;
		}
	}
	
	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
	public TrackWriterFactory getTrackWriterFactory() {
		return i_trackWriterFactory;
	}
	public void setTrackWriterFactory(TrackWriterFactory trackWriterFactory) {
		i_trackWriterFactory = trackWriterFactory;
	}

	public TitleFormatServiceFactory getTitleFormatServiceFactory() {
		return i_titleFormatServiceFactory;
	}

	public void setTitleFormatServiceFactory(
			TitleFormatServiceFactory titleFormatServiceFactory) {
		i_titleFormatServiceFactory = titleFormatServiceFactory;
	}
}
