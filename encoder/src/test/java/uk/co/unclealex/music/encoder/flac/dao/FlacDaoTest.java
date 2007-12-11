package uk.co.unclealex.music.encoder.flac.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;

import uk.co.unclealex.music.core.flac.FlacSpringTest;
import uk.co.unclealex.music.encoder.FlacAlbumBean;
import uk.co.unclealex.music.encoder.FlacArtistBean;
import uk.co.unclealex.music.encoder.FlacTrackBean;
import uk.co.unclealex.music.encoder.flac.FlacArtistDao;
import uk.co.unclealex.music.encoder.flac.FlacTrackDao;

public class FlacDaoTest extends FlacSpringTest {

	private FlacTrackDao i_flacTrackDao;
	private FlacArtistDao i_flacArtistDao;
	private DataSource i_flacDataSource;
	
	public void testFindByUrl() {
		String url = "file:///mnt/multimedia/flac/queen/queen_ii/10_funny_how_love_is.flac";
		FlacTrackBean flacTrackBean = getFlacTrackDao().findByUrl(url);
		FlacAlbumBean flacAlbumBean = flacTrackBean.getFlacAlbumBean();
		FlacArtistBean flacArtistBean = flacAlbumBean.getFlacArtistBean();
		
		assertEquals("The wrong url was returned.", url, flacTrackBean.getUrl());
		assertEquals("The wrong track code was returned.", "FUNNY HOW LOVE IS", flacTrackBean.getCode());
		assertEquals("The wrong album code was returned.", "QUEEN II", flacAlbumBean.getCode());
		assertEquals("The wrong track code was returned.", "QUEEN", flacArtistBean.getCode());
		
		assertEquals("The wrong track title was returned.", "Funny How Love Is", flacTrackBean.getTitle());
		assertEquals("The wrong album title was returned.", "Queen II", flacAlbumBean.getTitle());
		assertEquals("The wrong artist was returned.", "Queen", flacArtistBean.getName());
	}

	public void testFindAll() throws SQLException {
		SortedSet<FlacTrackBean> flacTrackBeans = new TreeSet<FlacTrackBean>(getFlacTrackDao().getAll());
		SortedSet<String> sqlUrls = new TreeSet<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Transformer<FlacTrackBean, String> urlTransformer = createUrlTransformer();
		try {
			conn = getFlacDataSource().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select url from tracks where content_type = 'flc' order by url");
			while (rs.next()) {
				sqlUrls.add(rs.getString("url"));
			}
			SortedSet<String> hibernateUrls = new TreeSet<String>();
			CollectionUtils.collect(flacTrackBeans, urlTransformer, hibernateUrls);
			testSame("hibernate urls", hibernateUrls, "sql urls", sqlUrls);
		}
		finally {
			DbUtils.closeQuietly(conn);
		}
	}

	public void testStartsWith() throws SQLException {
		for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
			SortedSet<FlacArtistBean> flacArtistBeans =
				new TreeSet<FlacArtistBean>(getFlacArtistDao().getArtistsBeginningWith(c));
			SortedSet<String> sqlNames = new TreeSet<String>();
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				conn = getFlacDataSource().getConnection();
				stmt = conn.prepareStatement("select name from contributors where namesort like ? order by namesort");
				stmt.setString(1, Character.toUpperCase(c) + "%");
				rs = stmt.executeQuery();
				while (rs.next()) {
					sqlNames.add(new String(rs.getBytes("name")));
				}
				SortedSet<String> hibernateNames = new TreeSet<String>();
				CollectionUtils.collect(flacArtistBeans, createNameTransformer(), hibernateNames);
				testSame("hibernate names", hibernateNames, "sql names", sqlNames);
			}
			finally {
				DbUtils.closeQuietly(conn);
			}
		}
	}
	
	public void testArtistsMatchTracks() {
		Transformer<FlacTrackBean, String> urlTransformer = createUrlTransformer();
		SortedSet<String> trackUrls = new TreeSet<String>();
		CollectionUtils.collect(getFlacTrackDao().getAll(), urlTransformer, trackUrls);
		
		SortedSet<String> artistUrls = new TreeSet<String>();
		for (FlacArtistBean flacArtistBean : getFlacArtistDao().getAll()) {
			for (FlacAlbumBean flacAlbumBean : flacArtistBean.getFlacAlbumBeans()) {
				CollectionUtils.collect(flacAlbumBean.getFlacTrackBeans(), urlTransformer, artistUrls);
			}
		}
		testSame("track urls", trackUrls, "artist urls", artistUrls);
	}
	
	protected void testSame(String name1, SortedSet<String> set1, String name2, SortedSet<String> set2) {
		SortedSet<String> remaining1 = new TreeSet<String>(set1);
		remaining1.removeAll(set2);

		SortedSet<String> remaining2 = new TreeSet<String>(set2);
		remaining2.removeAll(set1);

		if (!remaining1.isEmpty() || !remaining2.isEmpty()) {
			fail(
					"The following " + remaining1.size() + " " + name1 + " were not matched:\n  " +
					StringUtils.join(remaining1.iterator(), "\n  ") + '\n' +
					"The following " + remaining2.size() + " " + name2 + " were were not matched:\n  " +
					StringUtils.join(remaining2.iterator(), "\n  "));
		}		
	}
	
	protected Transformer<FlacTrackBean, String> createUrlTransformer() {
		return new Transformer<FlacTrackBean, String>() {
			public String transform(FlacTrackBean flacTrackBean) {
				return flacTrackBean.getUrl();
			}
		};
	}

	protected Transformer<FlacArtistBean, String> createNameTransformer() {
		return new Transformer<FlacArtistBean, String>() {
			public String transform(FlacArtistBean flacArtistBean) {
				return flacArtistBean.getName();
			}
		};
	}

	public FlacTrackDao getFlacTrackDao() {
		return i_flacTrackDao;
	}

	public void setFlacTrackDao(FlacTrackDao flacTrackDao) {
		i_flacTrackDao = flacTrackDao;
	}

	public DataSource getFlacDataSource() {
		return i_flacDataSource;
	}

	public void setFlacDataSource(DataSource flacDataSource) {
		i_flacDataSource = flacDataSource;
	}

	public FlacArtistDao getFlacArtistDao() {
		return i_flacArtistDao;
	}

	public void setFlacArtistDao(FlacArtistDao flacArtistDao) {
		i_flacArtistDao = flacArtistDao;
	}
}
