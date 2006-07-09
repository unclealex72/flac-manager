/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.File;
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
	
	private static String SQL_FLAC =
		"SELECT t.url as url, t.title as title, t.tracknum as trackNumber, t.year as year, a.title as album, c.name as artist, g.name as genre " +
		"FROM tracks t, albums a, contributors c, genre_track gt, genres g " +
		"WHERE t.album = a.id and a.contributor = c.id and t.id = gt.track and g.id = gt.genre and tracknum is not null and ct = 'flc'";
	
	private static String SQL_ARTIST =
		"SELECT t.url, min( length( t.url ) ) AS len, a.name FROM tracks t, contributors a " +
		"WHERE t.ct = 'dir' AND t.titlesearch = a.namesearch GROUP BY a.name";
	
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
			SortedSet<Track> tracks = (SortedSet<Track>) runner.query(conn, SQL_FLAC, new TrackHandler());
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
		public Object handle(ResultSet rs) throws SQLException {
			SortedSet<Track> tracks = new TreeSet<Track>();
			while (rs.next()) {
				String fileName = rs.getString("url");
				fileName = fileName.substring(FILE_PREFIX_LENGTH, fileName.length());
				tracks.add(new Track(
						new File(fileName),rs.getString("artist"), rs.getString("album"), rs.getString("title"),
						rs.getInt("trackNumber"), rs.getInt("year"), rs.getString("genre")));
			}
			return tracks;
		}
	}
	
	private class ArtistHandler implements ResultSetHandler {
		public Object handle(ResultSet rs) throws SQLException {
			Map<File,String> artists = new TreeMap<File, String>();
			while (rs.next()) {
				String fileName = rs.getString("url");
				fileName = fileName.substring(FILE_PREFIX_LENGTH, fileName.length());
				artists.put(new File(fileName), rs.getString("name"));
			}
			return artists;
		}
	}

}
