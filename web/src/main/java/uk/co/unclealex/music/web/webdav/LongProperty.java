package uk.co.unclealex.music.web.webdav;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.jackrabbit.value.LongValue;

public class LongProperty extends ReadOnlyProperty {

	private long i_lengthValue;

	public LongProperty(Node node, long length) {
		super(node);
		i_lengthValue = length;
	}

	@Override
	public boolean getBoolean() throws ValueFormatException, RepositoryException {
		throw longOnly();
	}

	@Override
	public Calendar getDate() throws ValueFormatException, RepositoryException {
		throw longOnly();
	}

	@Override
	public double getDouble() throws ValueFormatException, RepositoryException {
		throw longOnly();
	}

	@Override
	public long[] getLengths() throws ValueFormatException, RepositoryException {
		throw longOnly();
	}

	@Override
	public long getLong() throws ValueFormatException, RepositoryException {
		return getLengthValue();
	}

	@Override
	public InputStream getStream() throws RepositoryException {
		throw longOnly();
	}

	@Override
	public String getString() throws ValueFormatException, RepositoryException {
		throw longOnly();
	}

	protected ValueFormatException longOnly() {
		return new ValueFormatException("This property is of type " + PropertyType.TYPENAME_LONG);
	}

	@Override
	public int getType() throws RepositoryException {
		return PropertyType.LONG;
	}

	@Override
	public Value getValue() {
		return new LongValue(getLengthValue());
	}

	@Override
	public void accept(ItemVisitor visitor) throws RepositoryException {
		visitor.visit(this);
	}

	@Override
	public long getLength() {
		return Long.toString(getLengthValue()).length();
	}
	
	public long getLengthValue() {
		return i_lengthValue;
	}
}
