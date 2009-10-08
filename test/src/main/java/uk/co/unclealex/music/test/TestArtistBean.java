package uk.co.unclealex.music.test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

public class TestArtistBean implements Comparable<TestArtistBean> {

	private String i_name;
	private File i_file;
	private SortedSet<TestAlbumBean> i_testAlbumBeans = new TreeSet<TestAlbumBean>();
	
	public TestArtistBean(String name, File file) {
		super();
		i_name = name;
		i_file = file;
	}

	public TestAlbumBean addAlbum(String title, String path) {
		TestAlbumBean testAlbumBean = new TestAlbumBean(this, title, new File(getFile(), path));
		getTestAlbumBeans().add(testAlbumBean);
		return testAlbumBean;
	}

	@Override
	public int compareTo(TestArtistBean o) {
		return getName().compareTo(o.getName());
	}
	
	@Override
	public String toString() {
		StringWriter buffer = new StringWriter();
		PrintWriter writer = new PrintWriter(buffer);
		writer.println(getName());
		for (TestAlbumBean testAlbumBean : getTestAlbumBeans()) {
			String[] strs = StringUtils.split(testAlbumBean.toString(), '\n');
			for (String str : strs) {
				writer.println("  " + str);
			}
		}
		return buffer.toString();
	}
	
	public String getName() {
		return i_name;
	}
	
	public File getFile() {
		return i_file;
	}
	
	public SortedSet<TestAlbumBean> getTestAlbumBeans() {
		return i_testAlbumBeans;
	}
}
