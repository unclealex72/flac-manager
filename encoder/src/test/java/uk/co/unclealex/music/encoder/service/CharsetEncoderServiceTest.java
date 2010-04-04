package uk.co.unclealex.music.encoder.service;

import java.util.ArrayList;

import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.encoder.listener.EncodingEventListener;

public class CharsetEncoderServiceTest extends EncoderServiceTest {

	private FlacTrackDao i_flacTrackDao;
	
	@Override
	protected String getTestBase() {
		return "charset";
	}

	public void testInitialEncoding() throws Exception {
		doTestEncoding("Charset", new ArrayList<EncodingEventListener>());
		//doTestEncodingAndCheckActions("Inital Encoding", new ArrayList<EncodingAction>());
	}
	
	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}
}
