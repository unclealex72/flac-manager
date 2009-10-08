package uk.co.unclealex.music.test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.SortedSet;
import java.util.TreeSet;

public class TestAlbumBean implements Comparable<TestAlbumBean> {

	private TestArtistBean i_testArtistBean;
	private String i_title;
	private File i_file;
	private SortedSet<TestTrackBean> i_testTrackBeans = new TreeSet<TestTrackBean>();
	
	public TestAlbumBean(TestArtistBean testArtistBean, String title, File file) {
		super();
		i_testArtistBean = testArtistBean;
		i_title = title;
		i_file = file;
	}

	@Override
	public String toString() {
		StringWriter buffer = new StringWriter();
		PrintWriter writer = new PrintWriter(buffer);
		writer.println(getTitle());
		for (TestTrackBean testTrackBean : getTestTrackBeans()) {
			writer.println("  " + testTrackBean.toString());
		}
		return buffer.toString();
	}
	
	public TestAlbumBean addTrack(int trackNumber, String title, String path) {
		getTestTrackBeans().add(new TestTrackBean(this, trackNumber, title, new File(getFile(), path)));
		return this;
	}
	
	@Override
	public int compareTo(TestAlbumBean o) {
		return getTitle().compareTo(o.getTitle());
	}

	public TestAlbumBean addAlbum(String title, String path) {
		return getTestArtistBean().addAlbum(title, path);
	}

	public TestArtistBean getTestArtistBean() {
		return i_testArtistBean;
	}
	
	public String getTitle() {
		return i_title;
	}

	public File getFile() {
		return i_file;
	}
	
	public SortedSet<TestTrackBean> getTestTrackBeans() {
		return i_testTrackBeans;
	}
}
