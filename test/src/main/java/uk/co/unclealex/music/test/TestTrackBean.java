package uk.co.unclealex.music.test;

import java.io.File;

public class TestTrackBean implements Comparable<TestTrackBean> {

	private int i_trackNumber;
	private String i_title;
	private TestAlbumBean i_testAlbumBean;
	private File i_sourceFile;
	
	public TestTrackBean(TestAlbumBean testAlbumBean, int trackNumber, String title, File sourceFile) {
		super();
		i_testAlbumBean = testAlbumBean;
		i_trackNumber = trackNumber;
		i_title = title;
		i_sourceFile = sourceFile;
	}
	
	@Override
	public int compareTo(TestTrackBean o) {
		int cmp = getTrackNumber() - o.getTrackNumber();
		return cmp==0?getTitle().compareTo(o.getTitle()):cmp;
	}
	
	@Override
	public String toString() {
		return String.format("%02d. %s", getTrackNumber(), getTitle());
	}
	
	public int getTrackNumber() {
		return i_trackNumber;
	}
	
	public String getTitle() {
		return i_title;
	}
	
	public File getSourceFile() {
		return i_sourceFile;
	}
	
	public TestAlbumBean getTestAlbumBean() {
		return i_testAlbumBean;
	}
}
