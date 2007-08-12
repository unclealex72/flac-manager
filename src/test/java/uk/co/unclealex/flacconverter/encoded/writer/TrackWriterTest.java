package uk.co.unclealex.flacconverter.encoded.writer;

import java.io.IOException;
import java.util.Map;

import uk.co.unclealex.flacconverter.encoded.EncodedSpringTest;
import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.service.AlreadyEncodingException;
import uk.co.unclealex.flacconverter.encoded.service.CurrentlyScanningException;
import uk.co.unclealex.flacconverter.encoded.service.EncoderService;
import uk.co.unclealex.flacconverter.encoded.service.MultipleEncodingException;

public class TrackWriterTest extends EncodedSpringTest {

	private EncoderService i_encoderService;
	private TestTrackWriter i_testTrackWriter;
	private EncodedTrackDao i_encodedTrackDao;
	
	public void testWrite() throws IOException, AlreadyEncodingException, CurrentlyScanningException {
		try {
			getEncoderService().encodeAll(4);
		}
		catch (MultipleEncodingException e) {
			// Ignore.
		}
		TestTrackWriter writer = getTestTrackWriter();
		for (EncodedTrackBean encodedTrackBean : getEncodedTrackDao().getAll()) {
			writer.write(encodedTrackBean, trackStreams);
		}
		Map<String, Integer> fileNamesAndSizes = writer.getFileNamesAndSizes();
		System.out.println(fileNamesAndSizes);
		
		// {B/Brutal Truth/Extreme Conditions Demand Extreme Responses/07 - Collateral Damage.ogg=76383, B/Brutal Truth/Sounds Of The Animal Kingdomkill Trend Suicide/09 - Callous.mp3=269258, B/Brutal Truth/Sounds Of The Animal Kingdomkill Trend Suicide/09 - Callous.ogg=196096, N/Napalm Death/From Enslavement To Obliteration/12 - You Suffer.ogg=52324, N/Napalm Death/From Enslavement To Obliteration/12 - You Suffer.mp3=162041, B/Brutal Truth/Extreme Conditions Demand Extreme Responses/07 - Collateral Damage.mp3=101872, N/Napalm Death/Scum/24 - Your Achievement Bonus Track.mp3=149495, N/Napalm Death/Scum/25 - Dead Bonus Track.ogg=55080, N/Napalm Death/Scum/24 - Your Achievement Bonus Track.ogg=77204, S/Sod/Speak English Or Die/20 - The Ballad Of Jimi Hendrix.ogg=113367, S/Sod/Speak English Or Die/20 - The Ballad Of Jimi Hendrix.mp3=137588, S/Sod/Speak English Or Die/21 - Diamonds And Rust Extended Version.ogg=81596, N/Napalm Death/Scum/25 - Dead Bonus Track.mp3=123151, S/Sod/Speak English Or Die/21 - Diamonds And Rust Extended Version.mp3=134463}
	}
	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

	public TestTrackWriter getTestTrackWriter() {
		return i_testTrackWriter;
	}

	public void setTestTrackWriter(TestTrackWriter testTrackWriter) {
		i_testTrackWriter = testTrackWriter;
	}
	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}
	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
	
}
