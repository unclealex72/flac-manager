package uk.co.unclealex.flacconverter.encoded.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.io.IOUtils;

import uk.co.unclealex.flacconverter.encoded.EncodedSpringTest;
import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.dao.EncoderDao;
import uk.co.unclealex.flacconverter.encoded.dao.TrackDataDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.encoded.model.EncodingCommandBean;
import uk.co.unclealex.flacconverter.encoded.model.TrackDataBean;
import uk.co.unclealex.flacconverter.flac.dao.FlacTrackDao;
import uk.co.unclealex.flacconverter.flac.dao.SlimServerInformationDao;
import uk.co.unclealex.flacconverter.flac.dao.TestFlacProvider;
import uk.co.unclealex.flacconverter.flac.dao.TestSlimServerInformationDao;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class EncoderServiceTest extends EncodedSpringTest {

	public static final int SIMULTANEOUS_THREADS = 4;
	
	private FlacTrackDao i_flacTrackDao;
	private SlimServerInformationDao i_slimServerInformationDao;
	private EncoderService i_encoderService;
	private EncoderDao i_encoderDao;
	private EncodedTrackDao i_encodedTrackDao;
	private TrackDataDao i_trackDataDao;
	
	public void testEncode() throws IOException {
		EncoderService encoderService = getEncoderService();
		File file = getFlacTrackDao().getAllTracks().first().getFile();
		for (EncoderBean encoderBean : getEncoderDao().getAll()) {
			encoderService.encode(encoderBean, file, new MagicNumberCheckingEncodingClosure(encoderBean));
		}
	}

	protected class MagicNumberCheckingEncodingClosure implements EncodingClosure {
		private String magicNumber;
		private int length;
		private String extension;
		
		public MagicNumberCheckingEncodingClosure(EncoderBean encoderBean) {
			magicNumber = encoderBean.getMagicNumber();
			length = magicNumber.length();
			extension = encoderBean.getExtension();
		}
		
		public void process(InputStream in) throws IOException {
			StringBuffer buffer = new StringBuffer();
			int data;
			while ((data = in.read()) != -1) {
				if (buffer.length() < length) {
					String byterep = Integer.toHexString(data);
					if (byterep.length() == 1) {
						buffer.append('0');
					}
					buffer.append(byterep);
				}
			}
			assertEquals(
					"The " + extension + " data stream started with the wrong magic number.",
					magicNumber, buffer.toString());
		}
	}
	
	public void testEncodeAll() throws CurrentlyScanningException, AlreadyEncodingException, IOException {
		EncoderService encoderService = getEncoderService();
		SortedMap<EncodingCommandBean, Throwable> errors = null;
		Integer successful = null;
		try {
			successful = encoderService.encodeAll(SIMULTANEOUS_THREADS);
		}
		catch (MultipleEncodingException e) {
			successful = e.getTotalEncodedSuccessfully();
			errors = e.getExceptionsByEncodingCommandBean();
		}
		checkEncodingResult(successful, errors);
	}
	
	public void testOnlyOneEncoderRuns() throws InterruptedException, IOException {
		final int threadcount = 3;
		final EncoderService encoderService = getEncoderService();
		final List<SortedMap<EncodingCommandBean, Throwable>> errorsList = 
			new ArrayList<SortedMap<EncodingCommandBean,Throwable>>(threadcount);
		final List<Integer> successfulList = new ArrayList<Integer>(threadcount);
		final List<Boolean> runFlags = new ArrayList<Boolean>(threadcount);
		fillNull(errorsList, threadcount);
		fillNull(successfulList, threadcount);
		fillNull(runFlags, threadcount);
		final int[] indices = new int[threadcount];
		Thread[] threads = new Thread[threadcount];
		for (int idx = 0; idx < threadcount; idx++) {
			indices[idx] = idx;
		}
		for (int idx : indices) {
			final int thisIndex = idx;
			threads[idx] = new Thread() {
				@Override
				public void run() {
					try {
						successfulList.set(thisIndex, new Integer(encoderService.encodeAll(SIMULTANEOUS_THREADS)));
						runFlags.set(thisIndex, true);
					}
					catch (MultipleEncodingException e) {
						successfulList.set(thisIndex, e.getTotalEncodedSuccessfully());
						errorsList.set(thisIndex, e.getExceptionsByEncodingCommandBean());
						runFlags.set(thisIndex, true);
					}
					catch (AlreadyEncodingException e) {
						runFlags.set(thisIndex, false);
					}
					catch (CurrentlyScanningException e) {
						fail("The SlimServer is currently scanning.");
					}
				}
			};
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
		Integer successfulIndex = null;
		// find the index of the successful run and make sure only one was successful.
		for (int idx : indices) {
			if (runFlags.get(idx)) {
				assertNull("More than one thread ran at a time.", successfulIndex);
				successfulIndex = idx;
			}
		}
		assertNotNull("No threads ran.", successfulIndex);
		checkEncodingResult(successfulList.get(successfulIndex), errorsList.get(successfulIndex));
	}
	
	private void fillNull(List<? extends Object> list, int elements) {
		for (int idx = 0; idx < elements; idx++) {
			list.add(null);
		}
	}

	public void checkEncodingResult(Integer successful, SortedMap<EncodingCommandBean, Throwable> errors) throws IOException {
		assertNotNull("The number of successful encodings was not returned.", successful);
		assertNotNull("The map of unsuccessful encodings was not returned.", errors);
		
		SortedSet<EncoderUrlPair> expectedSuccesses = new TreeSet<EncoderUrlPair>();
		SortedSet<EncoderUrlPair> expectedFailures = new TreeSet<EncoderUrlPair>();
		
		SortedSet<EncoderBean> allEncoders = getEncoderDao().getAll();
		SortedSet<FlacTrackBean> allFlacTracks = getFlacTrackDao().getAllTracks();
		for (EncoderBean encoderBean : allEncoders) {
			for (FlacTrackBean flacTrackBean : allFlacTracks) {
				SortedSet<EncoderUrlPair> set =
					flacTrackBean.getUrl()==TestFlacProvider.MADE_UP_URL?expectedFailures:expectedSuccesses;
				set.add(new EncoderUrlPair(encoderBean.getExtension(), flacTrackBean.getUrl()));
			}
		}
		assertEquals("The wrong number of encodings succeeded.", new Integer(expectedSuccesses.size()), successful);
		SortedSet<EncoderUrlPair> actualFailures = new TreeSet<EncoderUrlPair>();
		CollectionUtils.collect(errors.keySet(), EncoderUrlPair.ENCODING_COMMAND_TRANSFORMER, actualFailures);
		assertEquals("The wrong encodings failed.", expectedFailures, actualFailures);
		
		SortedSet<EncodedTrackBean> allEncodedTracks = getEncodedTrackDao().getAll();
		SortedSet<EncoderUrlPair> actualSuccesses = new TreeSet<EncoderUrlPair>();
		CollectionUtils.collect(allEncodedTracks, EncoderUrlPair.ENCODED_TRACK_TRANSFORMER, actualSuccesses);
		assertEquals("The wrong encodings succeeded.", expectedSuccesses, actualSuccesses);
		for (EncodedTrackBean encodedTrackBean : getEncodedTrackDao().getAll()) {
			assertNotNull(
					"The " + encodedTrackBean.getEncoderBean().getExtension() + " encoding of " + 
					encodedTrackBean.getFlacUrl() + " is null.",
					encodedTrackBean.getTrackDataBean());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IOUtils.copy(encodedTrackBean.getTrack(), out);
			assertFalse(
					"The " + encodedTrackBean.getEncoderBean().getExtension() + " encoding of " + 
					encodedTrackBean.getFlacUrl() + " is empty.",
					out.toByteArray().length == 0);
		}
		assertEquals(
				"The number of tracks and the number of track data do not match.", 
				getEncodedTrackDao().getAll().size(),
				getTrackDataDao().getAll().size());
	}
	
	public void testOverwriteOlder() throws IOException, SQLException {
		testOverwrite(0, true);
	}
	
	public void testNotOverwriteNewer() throws IOException, SQLException {
		testOverwrite(new Date().getTime(), false);
	}

	public void testOverwrite(long time, boolean isOverwrite) throws IOException, SQLException {
		EncoderService encoderService = getEncoderService();
		TrackDataDao trackDataDao = getTrackDataDao();
		EncodingCommandBean encodingCommandBean = getFirstEncodingCommandBean();
		EncoderBean encoderBean = encodingCommandBean.getEncoderBean();
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		String url = encodingCommandBean.getFlacTrackBean().getUrl();
		EncodedTrackBean encodedTrackBean = new EncodedTrackBean();
		encodedTrackBean.setEncoderBean(encoderBean);
		encodedTrackBean.setFlacUrl(url);
		encodedTrackBean.setTimestamp(time);
		encodedTrackBean.setTrackDataBean(new TrackDataBean(trackDataDao.createBlob(new byte[0])));
		encodedTrackDao.store(encodedTrackBean);
		int expectedTrackDataCount = trackDataDao.getAll().size(); 
		boolean written = encoderService.encode(encodingCommandBean);
		EncodedTrackBean newEncodedTrackBean = encodedTrackDao.findByUrlAndEncoderBean(url, encoderBean);
		ByteArrayOutputStream trackStream = new ByteArrayOutputStream();
		InputStream in = newEncodedTrackBean.getTrack(); 
		IOUtils.copy(in, trackStream);
		in.close();
		byte[] track = trackStream.toByteArray();		
		assertEquals(
				"The old track id and new track id should be the same",
				encodedTrackBean.getId(), newEncodedTrackBean.getId());
		assertEquals(
				"The number of track data beans remaining is incorrect.",
				expectedTrackDataCount, trackDataDao.getAll().size());
		if (isOverwrite) {
			assertTrue("The track should have been overwritten.", written);
			assertFalse("The track data should not be empty.", track.length == 0);
		}
		else {
			assertFalse("The track should not have been overwritten.", written);
			assertEquals("The track data should be empty.", track.length, 0);
		}
	}

	public void testRemove() throws AlreadyEncodingException, IOException, CurrentlyScanningException {
		EncoderService encoderService = getEncoderService();
		try {
			encoderService.encodeAll(SIMULTANEOUS_THREADS);
		}
		catch (MultipleEncodingException e) {
			// This is expected.
		}
		EncodedTrackDao encodedTrackDao = getEncodedTrackDao();
		TrackDataDao trackDataDao = getTrackDataDao();
		SortedSet<EncodedTrackBean> expectedBeans = new TreeSet<EncodedTrackBean>(encodedTrackDao.getAll());
		EncodedTrackBean trackToRemove = new EncodedTrackBean(trackDataDao.createBlob(new byte[0]));
		trackToRemove.setFlacUrl("dummy");
		trackToRemove.setEncoderBean(getEncoderDao().getAll().first());
		trackToRemove.setTimestamp(0L);
		encodedTrackDao.store(trackToRemove);
		assertEquals("The wrong number of stale tracks was reported.", 1, encoderService.removeDeleted());
		assertEquals(
				"The wrong tracks were left untouched after deleting stale tracks.",
				expectedBeans, encodedTrackDao.getAll());
		assertEquals("The wrong number of track data beans were reported after deleting stale tracks.",
				expectedBeans.size(), getTrackDataDao().getAll().size());
	}
	
	public void testAbortOnScan() throws AlreadyEncodingException, MultipleEncodingException {
		Map<String, Long> information = new HashMap<String, Long>();
		information.put("isScanning", 1L);
		((TestSlimServerInformationDao) getSlimServerInformationDao()).setInformation(information);
		try {
			getEncoderService().encodeAll(SIMULTANEOUS_THREADS);
			fail("Encoding occurred even though the SlimServer was scanning.");
		}
		catch (CurrentlyScanningException e) {
			// Good!
		}
	}
	protected EncodingCommandBean getFirstEncodingCommandBean() {
		FlacTrackBean flacTrackBean = getFlacTrackDao().getAllTracks().first();
		EncoderBean encoderBean = getEncoderDao().getAll().first();
		return new EncodingCommandBean(encoderBean, flacTrackBean);
	}
	
	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

	public SlimServerInformationDao getSlimServerInformationDao() {
		return i_slimServerInformationDao;
	}

	public void setSlimServerInformationDao(
			SlimServerInformationDao slimServerInformationDao) {
		i_slimServerInformationDao = slimServerInformationDao;
	}

	public TrackDataDao getTrackDataDao() {
		return i_trackDataDao;
	}

	public void setTrackDataDao(TrackDataDao trackDataDao) {
		i_trackDataDao = trackDataDao;
	}
}
