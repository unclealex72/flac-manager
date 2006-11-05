/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

import com.mysql.jdbc.Driver;

/**
 * @author alex
 *
 */
public class FlacDAO implements FormatDAO {

	private static String FILE_PREFIX = "file://";
	private static int FILE_PREFIX_LENGTH = FILE_PREFIX.length();
	private static String BASE_URL = "file:///mnt/multimedia/flac/";
	private static int BASE_URL_LENGTH = BASE_URL.length();
	
	private static String ENCODING = "UTF-8";
	private static String SQL_FLAC =
		"SELECT distinct t.url as url, t.title as title, t.tracknum as trackNumber, t.year as year, a.title as album, c.name as artist, g.name as genre " +
		"FROM tracks t, albums a, contributors c, contributor_album ca, contributor_track ct, genre_track gt, genres g " +
		"WHERE t.album = a.id and a.id = ca.album and ca.contributor = c.id and t.id = ct.track and ct.contributor = c.id " +
			"and t.id = gt.track and g.id = gt.genre and tracknum is not null and t.content_type = 'flc'";	
	private static String SQL_ARTIST =
		"SELECT t.url, min( t.id ) AS id, c.name " +
		"FROM tracks t, albums a, contributors c, contributor_album ca " +
		"WHERE t.album = a.id and a.id = ca.album and c.id = ca.contributor " +
		"GROUP BY c.name";
	
	private static SQLException s_driverException = null;
	static {
		try {
			DriverManager.registerDriver(new Driver());
		} catch (SQLException e) {
			s_driverException = e;
		}
	}
	
	private static Connection getConnection() throws SQLException {
		if (s_driverException != null) {
			throw s_driverException;
		}
		return DriverManager.getConnection("jdbc:mysql://hurst/slimserver", "slimserver", "slimserver");
		
	}
	public IterableIterator<Track> findAllTracks(Logger log) {
		Connection conn = null;
		try {
			conn = getConnection();
			QueryRunner runner = new QueryRunner();
			SortedSet<Track> tracks = (SortedSet<Track>) runner.query(conn, SQL_FLAC, new TrackHandler(log));
			return new IterableIterator<Track>(tracks.iterator());
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	public Map<File,String> getAllArtists() {
		Connection conn = null;
		try {
			conn = getConnection();
			QueryRunner runner = new QueryRunner();
			Map<File,String> artists = (Map<File,String>) runner.query(conn, SQL_ARTIST, new ArtistHandler());
			return artists;
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		finally {
			DbUtils.closeQuietly(conn);
		}		
	}
	
	private class TrackHandler implements ResultSetHandler {
		private Logger i_log;
		public TrackHandler(Logger log) {
			i_log = log;
		}
		
		public Object handle(ResultSet rs) throws SQLException {
			try {
				SortedSet<Track> tracks = new TreeSet<Track>();
				while (rs.next()) {
					String fileName = rs.getString("url");
					fileName = fileName.substring(FILE_PREFIX_LENGTH, fileName.length());
					try {
						tracks.add(new Track(
								new File(fileName),
								new String(rs.getBytes("artist"), ENCODING), 
								new String(rs.getBytes("album"), ENCODING),
								new String(rs.getBytes("title"), ENCODING), 
								rs.getInt("trackNumber"), rs.getInt("year"),
								new String(rs.getBytes("genre"), ENCODING)));
					} catch (InvalidTrackException e) {
						i_log.warn(e);
					}
				}
				return tracks;
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
	}
	
	private class ArtistHandler implements ResultSetHandler {
		
		public ArtistHandler() {
		}
		
		public Object handle(ResultSet rs) throws SQLException {
			Map<File,String> artists = new TreeMap<File, String>();
			File baseDir = new File(BASE_URL.substring(FILE_PREFIX_LENGTH, BASE_URL_LENGTH));
			try {
				while (rs.next()) {
					String fileName = rs.getString("url");
					fileName = fileName.substring(BASE_URL_LENGTH);
					if (fileName.charAt(0) == '/') {
						fileName = fileName.substring(1);
					}
					String artistDir = fileName.substring(0, fileName.indexOf('/'));
					String name = new String(rs.getBytes("name"), ENCODING);
					artists.put(new File(baseDir, artistDir), name);
				}
			} catch (UnsupportedEncodingException e) {
				return null;
			}
			return artists;
		}
	}

}
