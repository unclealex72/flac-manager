package uk.co.unclealex.music.web.webdav;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.jackrabbit.JcrConstants;

import uk.co.unclealex.music.core.service.filesystem.RepositoryManager;

public class DataProperty extends ReadOnlyProperty {

	private RepositoryManager i_repositoryManager;
	
	public DataProperty(Node node, RepositoryManager repositoryManager) {
		super(node);
		i_repositoryManager = repositoryManager;
	}

	@Override
	public boolean getBoolean() throws ValueFormatException, RepositoryException {
		throw streamOnly();
	}

	@Override
	public Calendar getDate() throws ValueFormatException, RepositoryException {
		throw streamOnly();
	}

	@Override
	public double getDouble() throws ValueFormatException, RepositoryException {
		throw streamOnly();
	}

	@Override
	public long[] getLengths() throws ValueFormatException, RepositoryException {
		throw streamOnly();
	}

	@Override
	public long getLong() throws ValueFormatException, RepositoryException {
		throw streamOnly();
	}

	@Override
	public InputStream getStream() throws RepositoryException {
		return getRepositoryManager().createInputStream(getNode());
	}

	@Override
	public String getString() throws ValueFormatException, RepositoryException {
		throw streamOnly();
	}

	protected ValueFormatException streamOnly() {
		return new ValueFormatException("This property is of type " + PropertyType.TYPENAME_BINARY);
	}

	@Override
	public int getType() throws RepositoryException {
		return PropertyType.BINARY;
	}

	@Override
	public Value getValue() {
		return new StreamValue(getNode(), getRepositoryManager());
	}

	@Override
	public Value[] getValues() {
		return new Value[] { getValue() };
	}

	@Override
	public void accept(ItemVisitor visitor) throws RepositoryException {
		visitor.visit(this);
	}

	@Override
	public Item getAncestor(int depth) throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		return getParent().getAncestor(depth - 1);
	}

	@Override
	public int getDepth() throws RepositoryException {
		return getParent().getDepth() + 1;
	}

	@Override
	public String getName() throws RepositoryException {
		return JcrConstants.JCR_DATA;
	}

	@Override
	public Node getParent() throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		return getNode();
	}

	@Override
	public String getPath() throws RepositoryException {
		return getNode().getName() + '/' + getName();
	}

	@Override
	public Session getSession() throws RepositoryException {
		return getNode().getSession();
	}

	@Override
	public long getLength() throws ValueFormatException, RepositoryException {
		return getRepositoryManager().getLength(getNode());
	}
	
	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public boolean isSame(Item otherItem) throws RepositoryException {
		return this == otherItem;
	}

	public RepositoryManager getRepositoryManager() {
		return i_repositoryManager;
	}
}
