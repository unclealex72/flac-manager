package uk.co.unclealex.music.base.io;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.support.AbstractLobType;

public class BlobUserType extends AbstractLobType {

 public int[] sqlTypes() {
   return new int[] { Types.BLOB, Types.INTEGER };
 }

 public Class<?> returnedClass() {
   return KnownLengthInputStream.class;
 }

 protected Object nullSafeGetInternal(ResultSet rs, String[] names, Object owner, LobHandler lobHandler)
  throws SQLException, HibernateException {
	 int length = rs.getInt(names[1]);
	 if (rs.wasNull()) {
		 return null;
	 }
	 InputStream in = lobHandler.getBlobAsBinaryStream(rs, names[0]);
   return new KnownLengthInputStream(in, length);
 }

 protected void nullSafeSetInternal(PreparedStatement ps, int index, Object value, LobCreator lobCreator)
  throws SQLException, HibernateException {
   if (value != null) {
	   KnownLengthInputStream in = (KnownLengthInputStream) value;
	   int length = in.getLength();
	   lobCreator.setBlobAsBinaryStream(ps, index, in, length);
	   ps.setInt(index + 1, length);
   } else {
    lobCreator.setBlobAsBytes(ps, index, null);
    ps.setNull(index, Types.INTEGER);
   }
 }
}