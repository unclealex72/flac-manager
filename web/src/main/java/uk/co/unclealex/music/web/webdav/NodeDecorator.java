package uk.co.unclealex.music.web.webdav;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;

public class NodeDecorator implements Node {

	private Node i_delegate;

	public NodeDecorator(Node delegate) {
		super();
		i_delegate = delegate;
	}

	public void accept(ItemVisitor visitor) throws RepositoryException {
		visitor.visit(this);
	}

	public void addMixin(String mixinName) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		getDelegate().addMixin(mixinName);
	}

	public Node addNode(String relPath, String primaryNodeTypeName)
			throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().addNode(relPath, primaryNodeTypeName);
	}

	public Node addNode(String relPath) throws ItemExistsException,
			PathNotFoundException, VersionException,
			ConstraintViolationException, LockException, RepositoryException {
		return getDelegate().addNode(relPath);
	}

	public boolean canAddMixin(String mixinName)
			throws NoSuchNodeTypeException, RepositoryException {
		return getDelegate().canAddMixin(mixinName);
	}

	public void cancelMerge(Version version) throws VersionException,
			InvalidItemStateException, UnsupportedRepositoryOperationException,
			RepositoryException {
		getDelegate().cancelMerge(version);
	}

	public Version checkin() throws VersionException,
			UnsupportedRepositoryOperationException, InvalidItemStateException,
			LockException, RepositoryException {
		return getDelegate().checkin();
	}

	public void checkout() throws UnsupportedRepositoryOperationException,
			LockException, RepositoryException {
		getDelegate().checkout();
	}

	public void doneMerge(Version version) throws VersionException,
			InvalidItemStateException, UnsupportedRepositoryOperationException,
			RepositoryException {
		getDelegate().doneMerge(version);
	}

	public Item getAncestor(int depth) throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		return getDelegate().getAncestor(depth);
	}

	public Version getBaseVersion()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		return getDelegate().getBaseVersion();
	}

	public String getCorrespondingNodePath(String workspaceName)
			throws ItemNotFoundException, NoSuchWorkspaceException,
			AccessDeniedException, RepositoryException {
		return getDelegate().getCorrespondingNodePath(workspaceName);
	}

	public NodeDefinition getDefinition() throws RepositoryException {
		return getDelegate().getDefinition();
	}

	public int getDepth() throws RepositoryException {
		return getDelegate().getDepth();
	}

	public int getIndex() throws RepositoryException {
		return getDelegate().getIndex();
	}

	public Lock getLock() throws UnsupportedRepositoryOperationException,
			LockException, AccessDeniedException, RepositoryException {
		return getDelegate().getLock();
	}

	public NodeType[] getMixinNodeTypes() throws RepositoryException {
		return getDelegate().getMixinNodeTypes();
	}

	public String getName() throws RepositoryException {
		return getDelegate().getName();
	}

	public Node getNode(String relPath) throws PathNotFoundException,
			RepositoryException {
		return getDelegate().getNode(relPath);
	}

	public NodeIterator getNodes() throws RepositoryException {
		return getDelegate().getNodes();
	}

	public NodeIterator getNodes(String namePattern) throws RepositoryException {
		return getDelegate().getNodes(namePattern);
	}

	public Node getParent() throws ItemNotFoundException,
			AccessDeniedException, RepositoryException {
		return getDelegate().getParent();
	}

	public String getPath() throws RepositoryException {
		return getDelegate().getPath();
	}

	public Item getPrimaryItem() throws ItemNotFoundException,
			RepositoryException {
		return getDelegate().getPrimaryItem();
	}

	public NodeType getPrimaryNodeType() throws RepositoryException {
		return getDelegate().getPrimaryNodeType();
	}

	public PropertyIterator getProperties() throws RepositoryException {
		return getDelegate().getProperties();
	}

	public PropertyIterator getProperties(String namePattern)
			throws RepositoryException {
		return getDelegate().getProperties(namePattern);
	}

	public Property getProperty(String relPath) throws PathNotFoundException,
			RepositoryException {
		return getDelegate().getProperty(relPath);
	}

	public PropertyIterator getReferences() throws RepositoryException {
		return getDelegate().getReferences();
	}

	public Session getSession() throws RepositoryException {
		return getDelegate().getSession();
	}

	public String getUUID() throws UnsupportedRepositoryOperationException,
			RepositoryException {
		return getDelegate().getUUID();
	}

	public VersionHistory getVersionHistory()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		return getDelegate().getVersionHistory();
	}

	public boolean hasNode(String relPath) throws RepositoryException {
		return getDelegate().hasNode(relPath);
	}

	public boolean hasNodes() throws RepositoryException {
		return getDelegate().hasNodes();
	}

	public boolean hasProperties() throws RepositoryException {
		return getDelegate().hasProperties();
	}

	public boolean hasProperty(String relPath) throws RepositoryException {
		return getDelegate().hasProperty(relPath);
	}

	public boolean holdsLock() throws RepositoryException {
		return getDelegate().holdsLock();
	}

	public boolean isCheckedOut() throws RepositoryException {
		return getDelegate().isCheckedOut();
	}

	public boolean isLocked() throws RepositoryException {
		return getDelegate().isLocked();
	}

	public boolean isModified() {
		return getDelegate().isModified();
	}

	public boolean isNew() {
		return getDelegate().isNew();
	}

	public boolean isNode() {
		return getDelegate().isNode();
	}

	public boolean isNodeType(String nodeTypeName) throws RepositoryException {
		return getDelegate().isNodeType(nodeTypeName);
	}

	public boolean isSame(Item otherItem) throws RepositoryException {
		return getDelegate().isSame(otherItem);
	}

	public Lock lock(boolean isDeep, boolean isSessionScoped)
			throws UnsupportedRepositoryOperationException, LockException,
			AccessDeniedException, InvalidItemStateException,
			RepositoryException {
		return getDelegate().lock(isDeep, isSessionScoped);
	}

	public NodeIterator merge(String srcWorkspace, boolean bestEffort)
			throws NoSuchWorkspaceException, AccessDeniedException,
			MergeException, LockException, InvalidItemStateException,
			RepositoryException {
		return getDelegate().merge(srcWorkspace, bestEffort);
	}

	public void orderBefore(String srcChildRelPath, String destChildRelPath)
			throws UnsupportedRepositoryOperationException, VersionException,
			ConstraintViolationException, ItemNotFoundException, LockException,
			RepositoryException {
		getDelegate().orderBefore(srcChildRelPath, destChildRelPath);
	}

	public void refresh(boolean keepChanges) throws InvalidItemStateException,
			RepositoryException {
		getDelegate().refresh(keepChanges);
	}

	public void remove() throws VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		getDelegate().remove();
	}

	public void removeMixin(String mixinName) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		getDelegate().removeMixin(mixinName);
	}

	public void restore(String versionName, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		getDelegate().restore(versionName, removeExisting);
	}

	public void restore(Version version, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			RepositoryException {
		getDelegate().restore(version, removeExisting);
	}

	public void restore(Version version, String relPath, boolean removeExisting)
			throws PathNotFoundException, ItemExistsException,
			VersionException, ConstraintViolationException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		getDelegate().restore(version, relPath, removeExisting);
	}

	public void restoreByLabel(String versionLabel, boolean removeExisting)
			throws VersionException, ItemExistsException,
			UnsupportedRepositoryOperationException, LockException,
			InvalidItemStateException, RepositoryException {
		getDelegate().restoreByLabel(versionLabel, removeExisting);
	}

	public void save() throws AccessDeniedException, ItemExistsException,
			ConstraintViolationException, InvalidItemStateException,
			ReferentialIntegrityException, VersionException, LockException,
			NoSuchNodeTypeException, RepositoryException {
		getDelegate().save();
	}

	public Property setProperty(String name, boolean value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, value);
	}

	public Property setProperty(String name, Calendar value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, value);
	}

	public Property setProperty(String name, double value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, value);
	}

	public Property setProperty(String name, InputStream value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, value);
	}

	public Property setProperty(String name, long value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, value);
	}

	public Property setProperty(String name, Node value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, value);
	}

	public Property setProperty(String name, String value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, value, type);
	}

	public Property setProperty(String name, String value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, value);
	}

	public Property setProperty(String name, String[] values, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, values, type);
	}

	public Property setProperty(String name, String[] values)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, values);
	}

	public Property setProperty(String name, Value value, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, value, type);
	}

	public Property setProperty(String name, Value value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, value);
	}

	public Property setProperty(String name, Value[] values, int type)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, values, type);
	}

	public Property setProperty(String name, Value[] values)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		return getDelegate().setProperty(name, values);
	}

	public void unlock() throws UnsupportedRepositoryOperationException,
			LockException, AccessDeniedException, InvalidItemStateException,
			RepositoryException {
		getDelegate().unlock();
	}

	public void update(String srcWorkspaceName)
			throws NoSuchWorkspaceException, AccessDeniedException,
			LockException, InvalidItemStateException, RepositoryException {
		getDelegate().update(srcWorkspaceName);
	}
	
	public Node getDelegate() {
		return i_delegate;
	}
}
