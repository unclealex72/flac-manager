package uk.co.unclealex.music.web.webdav;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import uk.co.unclealex.music.core.service.filesystem.RepositoryManager;

public class StreamValue implements Value {

	private boolean i_accessed = false;
	private Node i_node;
	private RepositoryManager i_repositoryManager;
	
	public StreamValue(Node node, RepositoryManager repositoryManager) {
		super();
		i_node = node;
		i_repositoryManager = repositoryManager;
	}

	@Override
	public InputStream getStream() throws IllegalStateException, RepositoryException {
		checkAccess();
		return getRepositoryManager().createInputStream(getNode());
	}

	protected ValueFormatException streamOnly() {
		checkAccess();
		return new ValueFormatException("This property is of type " + PropertyType.TYPENAME_BINARY);
	}

	protected synchronized void checkAccess() {
		if (isAccessed()) {
			throw new IllegalStateException("This value has already been accessed.");
		}
	}
	
	@Override
	public int getType() {
		return PropertyType.BINARY;
	}

	@Override
	public boolean getBoolean() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		throw streamOnly();
	}

	@Override
	public Calendar getDate() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		throw streamOnly();
	}

	@Override
	public double getDouble() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		throw streamOnly();
	}

	@Override
	public long getLong() throws ValueFormatException, IllegalStateException,
			RepositoryException {
		throw streamOnly();
	}

	@Override
	public String getString() throws ValueFormatException,
			IllegalStateException, RepositoryException {
		throw streamOnly();
	}

	protected boolean isAccessed() {
		return i_accessed;
	}

	protected void setAccessed(boolean accessed) {
		i_accessed = accessed;
	}

	protected Node getNode() {
		return i_node;
	}
	
	protected RepositoryManager getRepositoryManager() {
		return i_repositoryManager;
	}

}
