

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import com.mysql.jdbc.Driver;


/**
 * @author alex
 *
 */
public class EncodingTest {

	private static String SQL_ARTIST =
		"SELECT name from contributors where name like 'Mot%'";
	
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
	
	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = getConnection();
			QueryRunner runner = new QueryRunner();
			runner.query(conn, SQL_ARTIST, new ArtistHandler());
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	private static class ArtistHandler implements ResultSetHandler {
		public Object handle(ResultSet rs) throws SQLException {
			while (rs.next()) {
				byte[] bytes = rs.getBytes("name");
				try {
					String name = new String(bytes, "UTF-8");
					System.out.println(name + " : " + rs.getString("name"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				for (byte by : bytes) {
					int b = by < 0?by + 256:by;
					System.out.println(Integer.toHexString(b));
				}
			}
			return null;
		}
	}
}
