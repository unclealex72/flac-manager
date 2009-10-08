package uk.co.unclealex.music.encoder.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import uk.co.unclealex.music.base.dao.FlacTrackDao;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.base.service.titleformat.TitleFormatService;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.action.FileAddedAction;
import uk.co.unclealex.music.encoder.action.FileRemovedAction;
import uk.co.unclealex.music.encoder.action.TrackEncodedAction;
import uk.co.unclealex.music.encoder.action.TrackOwnedAction;
import uk.co.unclealex.music.encoder.action.TrackRemovedAction;
import uk.co.unclealex.music.encoder.exception.EncodingException;
import uk.co.unclealex.music.test.TestFlacProvider;

public class GenericEncoderServiceTest extends EncoderServiceTest {

	private FlacTrackDao i_flacTrackDao;
	
	@Override
	protected String getTestBase() {
		return "generic";
	}

	public void testInitialEncoding() throws EncodingException {
		doTestEncoding("Inital Encoding");
	}
	
	public void testUpdate() throws Exception {
		String keepYourselfAlive = "queen/queen/01_keep_yourself_alive.flac";
		TestFlacProvider testFlacProvider = getTestFlacProvider();
		testFlacProvider.touchFiles(keepYourselfAlive);
		String keepYourselfAliveUrl = new File(testFlacProvider.getWorkingDirectory(), keepYourselfAlive).toURI().toString();
		List<EncodingAction> expectedActions = new ArrayList<EncodingAction>();
		TitleFormatService titleFormatService = getTitleFormatService();
		for (EncodedTrackBean encodedTrackBean : getEncodedTrackDao().findByUrl(keepYourselfAliveUrl)) {
			expectedActions.add(new TrackRemovedAction(encodedTrackBean));
			SortedSet<OwnerBean> ownerBeans = encodedTrackBean.getOwnerBeans();
			for (OwnerBean ownerBean : ownerBeans) {
				String path = titleFormatService.createTitle(encodedTrackBean, ownerBean); 
				expectedActions.add(new FileRemovedAction(path, encodedTrackBean));
			}
			expectedActions.add(new TrackEncodedAction(encodedTrackBean));
			for (OwnerBean ownerBean : ownerBeans) {
				expectedActions.add(new TrackOwnedAction(ownerBean, encodedTrackBean));
			  String path = titleFormatService.createTitle(encodedTrackBean, ownerBean); 
				expectedActions.add(new FileAddedAction(path, encodedTrackBean));
			}
			expectedActions.add(new TrackEncodedAction(encodedTrackBean));
		}
		doTestEncoding("Update", expectedActions);
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}
}
