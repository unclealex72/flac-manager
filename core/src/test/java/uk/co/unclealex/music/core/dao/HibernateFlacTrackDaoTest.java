package uk.co.unclealex.music.core.dao;

import junit.framework.TestCase;

public class HibernateFlacTrackDaoTest extends TestCase {

	public void testDenormaliseChange() {
		doTestDenormalise("file:/mnt/home/alex", "file:///mnt/home/alex");
	}
	
	public void testDenormaliseNoChange() {
		doTestDenormalise("file:///mnt/home/alex", "file:///mnt/home/alex");		
	}
	
	protected void doTestDenormalise(String original, String expected) {
		HibernateFlacTrackDao hibernateFlacTrackDao = new HibernateFlacTrackDao();
		String denormalised = hibernateFlacTrackDao.denormalise(original);
		assertEquals("The url " + original + " was not correctly denormalised.", expected, denormalised);
	}
}
