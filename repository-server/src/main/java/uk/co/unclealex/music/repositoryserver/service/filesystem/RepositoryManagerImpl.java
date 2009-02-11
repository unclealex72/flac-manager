package uk.co.unclealex.music.repositoryserver.service.filesystem;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;

import org.apache.commons.io.FilenameUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

import uk.co.unclealex.music.base.service.filesystem.FileCommandBean;
import uk.co.unclealex.music.base.service.filesystem.Jcr170Escaper;
import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;

@Transactional
public class RepositoryManagerImpl<E> extends JcrTemplate implements RepositoryManager {

	private static final Logger log = Logger.getLogger(RepositoryManagerImpl.class);
		
	private RepositoryAdaptor<E> i_repositoryAdaptor;
	
	@Override
	public Set<Node> find(final int id) {
		JcrCallback<Set<Node>> callback = new FindByIdJcrCallback<Set<Node>>(id) {
			@Override
			public Set<Node> doInJcr(Session session) throws RepositoryException {
				return findById(session);
			}
		};
		return execute(callback);
	}
	
	@Override
	public int remove(int id) {
		JcrCallback<Integer> callback = new FindByIdJcrCallback<Integer>(id) {
			@Override
			public Integer doInJcr(Session session) throws RepositoryException {
				Set<Node> foundNodes = findById(session);
				int removed = foundNodes.size();
				for (Node node : foundNodes) {
					remove(node);
				}
				return removed;

			}
		};
		return execute(callback);
	}
	
	protected void remove(Node node) throws RepositoryException {
		if (node.getDepth() != 0) {
			Node parent = node.getParent();
			log.info("Removing " + node.getPath());
			node.remove();
			if (!parent.getNodes().hasNext()) {
				remove(parent);
			}
		}
	}

	@Override
	public void refresh() {
		JcrCallback<Object> callback = new ClearJcrCallback<Object>() {
			public Object doInJcr(Session session) throws RepositoryException {
				clear(session);
				Node rootNode = session.getRootNode();
				setLastModifiedDate(rootNode, new Date());
				RepositoryAdaptor<E> repositoryAdaptor = getRepositoryAdaptor();
				for (E element : repositoryAdaptor.getAllElements()) {
					addNodes(session, repositoryAdaptor.transform(element), false);
				}
				if (log.isDebugEnabled()) {
					showRepository(session);
				}
				return null;
			}
		};
		execute(callback);
	}

	@Override
	public void clear() {
		JcrCallback<Object> callback = new ClearJcrCallback<Object>() {
			public Object doInJcr(Session session) throws RepositoryException {
				clear(session);
				Node rootNode = session.getRootNode();
				setLastModifiedDate(rootNode, new Date());
				return null;
			}
		};
		execute(callback);
	}

	protected void showRepository(Session session) throws RepositoryException {
		log.debug("The following nodes are in the reposiory:");
		showRepository(session.getRootNode());
	}
	
	
	protected void showRepository(Node node) throws RepositoryException {
		log.debug("Node " + node.getPath());
		for (NodeIterator iter = node.getNodes(); iter.hasNext(); ) {
			showRepository(iter.nextNode());
		}
	}

	protected void addNodes(Session session, Collection<FileCommandBean> fileBeans, boolean removeExisting) throws RepositoryException {
		for (FileCommandBean fileBean : fileBeans) {
			addNode(session, fileBean, removeExisting);
		}
	}

	@Override
	public void add(int id) {
		add(id, false);
	}
	
	@Override
	public void addOrUpdate(int id) {
		add(id, true);
	}
	
	protected void add(final int id, final boolean removeExisting) {
		JcrCallback<Object> callback = new JcrCallback<Object>() {
			@Override
			public Object doInJcr(Session session) throws RepositoryException {
				E element = getRepositoryAdaptor().findById(id);
				if (element != null) {
					addNodes(session, getRepositoryAdaptor().transform(element), removeExisting);
				}
				return null;
			}
		};
		execute(callback);
	}
	
	protected Node addNode(Session session, FileCommandBean fileBean, boolean removeExisting) throws RepositoryException {
		String path = fileBean.getPath();
		path = Jcr170Escaper.escape(path);
		if (removeExisting) {
			Node existingNode;
			try {
				existingNode = getSession().getRootNode().getNode(path);
				log.info("Removing " + path);
				existingNode.remove();
			}
			catch (PathNotFoundException e) {
				// Ignore
			}
		}
		log.info("Caching " + path);
		String name = FilenameUtils.getName(path);
		String directory = FilenameUtils.getPath(path);
		Node directoryNode = findOrCreateDirectory(session, directory);
		Node fileNode = directoryNode.addNode(name, JcrConstants.NT_FILE);
		Node node = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_UNSTRUCTURED);
		setLastModifiedDate(node, fileBean.getLastModifiedDate());
		String mimeType = fileBean.getMimeType();
		if (mimeType != null) {
			node.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
		}
		node.setProperty(PROPERTY_LENGTH, fileBean.getLength());
		node.setProperty(PROPERTY_ID, fileBean.getId());
		getSession().save();
		return fileNode;
	}
	
	protected void setLastModifiedDate(Node node, Date date) throws RepositoryException {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		node.setProperty(JcrConstants.JCR_LASTMODIFIED, calendar);		
	}
	
	protected Node findOrCreateDirectory(Session session, String directory) throws RepositoryException {
		if (directory.endsWith("/")) {
			directory = directory.substring(0, directory.length() - 1);
		}
		Node rootNode = session.getRootNode();
		if (directory.isEmpty()) {
			return rootNode;
		}
		try {
			return rootNode.getNode(directory);
		}
		catch (javax.jcr.PathNotFoundException e) {
			String parentDirectory = FilenameUtils.getPath(directory);
			String directoryName = FilenameUtils.getName(directory);
			Node parentNode = findOrCreateDirectory(session, parentDirectory);
			Node node = parentNode.addNode(directoryName, JcrConstants.NT_FOLDER);
			return node;
		}
	}

	@Override
	public int getObjectId(final Node node) {
		JcrCallback<Integer> callback = new JcrCallback<Integer>() {
			@Override
			public Integer doInJcr(Session session) throws RepositoryException {
				Property property = node.getProperty(PROPERTY_ID);
				return (int) property.getValue().getLong();
			}
		};
		return execute(callback);
	}

	@Override
	public long getLength(final Node node) {
		JcrCallback<Long> callback = new JcrCallback<Long>() {
			@Override
			public Long doInJcr(Session session) throws RepositoryException {
				Property property = node.getProperty(PROPERTY_LENGTH);
				try {
					return property.getLong();
				}
				catch (ValueFormatException e) {
					throw new RepositoryException(e);
				}
			}
		};
		return execute(callback);
	}
	
	public RepositoryAdaptor<E> getRepositoryAdaptor() {
		return i_repositoryAdaptor;
	}

	@Required
	public void setRepositoryAdaptor(
			RepositoryAdaptor<E> pathInformationBeanAdaptor) {
		i_repositoryAdaptor = pathInformationBeanAdaptor;
	}
}
