package uk.co.unclealex.music.web.webdav;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.JcrConstants;

public abstract class ReadOnlyProperty implements Property {

	private PropertyDefinition i_definition;
	private Node i_node;
	
	public ReadOnlyProperty(Node node) {
		i_node = node;
		i_definition = new SimplePropertyDefinition(this);
	}
	
	protected ConstraintViolationException readOnly() {
		return new ConstraintViolationException("This property is read-only.");
	}
	
	@Override
	public void setValue(Value value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw readOnly();
	}

	@Override
	public void setValue(Value[] values) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw readOnly();
	}

	@Override
	public void setValue(String value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw readOnly();
	}

	@Override
	public void setValue(String[] values) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw readOnly();
	}

	@Override
	public void setValue(InputStream value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw readOnly();
	}

	@Override
	public void setValue(long value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw readOnly();
	}

	@Override
	public void setValue(double value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw readOnly();
	}

	@Override
	public void setValue(Calendar value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw readOnly();
	}

	@Override
	public void setValue(boolean value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw readOnly();
	}

	@Override
	public void setValue(Node value) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException,
			RepositoryException {
		throw readOnly();
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public boolean isNode() {
		return false;
	}

	@Override
	public void refresh(boolean keepChanges) throws InvalidItemStateException,
			RepositoryException {
		// Do nothing
	}

	@Override
	public void remove() throws VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		throw readOnly();
	}

	@Override
	public void save() throws AccessDeniedException, ItemExistsException,
			ConstraintViolationException, InvalidItemStateException,
			ReferentialIntegrityException, VersionException, LockException,
			NoSuchNodeTypeException, RepositoryException {
		throw readOnly();
	}

	public PropertyDefinition getDefinition() {
		return i_definition;
	}

	@Override
	public Node getNode() {
		return i_node;
	}

	public abstract Value getValue();
	
	@Override
	public Value[] getValues() {
		return new Value[] { getValue() };
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
	public boolean isNew() {
		return false;
	}

	@Override
	public boolean isSame(Item otherItem) throws RepositoryException {
		return this == otherItem;
	}

}
