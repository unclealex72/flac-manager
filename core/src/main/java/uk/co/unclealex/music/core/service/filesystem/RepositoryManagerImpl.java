package uk.co.unclealex.music.core.service.filesystem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.ValueFormatException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.commons.io.FilenameUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.TransientRepository.RepositoryFactory;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.io.DataExtractor;
import uk.co.unclealex.music.core.io.InputStreamCopier;
import uk.co.unclealex.music.core.io.KnownLengthOutputStream;

@Transactional
public class RepositoryManagerImpl<E> implements RepositoryManager {

	private static final Logger log = Logger.getLogger(RepositoryManagerImpl.class);
	
	private Lock i_lock = new ReentrantLock();
	private String i_repositoryRootPath;
	private Session i_session;
	private Repository i_repository;
	private String i_baseDirectory;
	
	private InputStreamCopier i_inputStreamCopier;
	private RepositoryAdaptor<E> i_repositoryAdaptor;
	private DataExtractor i_dataExtractor;

	@PostConstruct
	public void initialise() throws IOException, URISyntaxException, RepositoryException {
		File home = getRepositoryDirectory();
		boolean homeExists = home.isDirectory();
		final RepositoryConfig config =
			RepositoryConfig.create(
				TransientRepository.class.getResource("repository.xml").toURI(), home.getCanonicalPath());
		RepositoryFactory factory = new RepositoryFactory() {
			@Override
			public RepositoryImpl getRepository() throws RepositoryException {
				return new ManagedRepositoryImpl(config, RepositoryManagerImpl.this);
			}
		};
		Repository repository = new TransientRepository(factory);
		Session session = repository.login(new SimpleCredentials("username", "password".toCharArray()));
		setSession(session);
		setRepository(repository);
		if (!homeExists) {
			log.info("Refreshing as no previous repository was found at " + home);
			refresh();
		}
	}

	protected File getRepositoryDirectory() {
		return new File(getBaseDirectory() + '/' + getRepositoryRootPath());
	}
	
	@PreDestroy
	public void destroy() {
		getSession().logout();
	}

	@Override
	public Set<Node> find(int id) throws RepositoryException {
		Session session = getSession();
		Set<Node> foundNodes = new HashSet<Node>();
		Query query = session.getWorkspace().getQueryManager().createQuery(
				"//" + JcrConstants.JCR_CONTENT + "[@" + PROPERTY_ID + "='" + id + "']", Query.XPATH);
		QueryResult queryResult = query.execute();
		for (NodeIterator iter = queryResult.getNodes(); iter.hasNext(); ) {
			Node node = iter.nextNode();
			foundNodes.add(node.getParent());
		}
		return foundNodes;
	}
	
	@Override
	public int remove(int id) throws RepositoryException {
		Set<Node> foundNodes = find(id);
		int removed = foundNodes.size();
		for (Node node : foundNodes) {
			remove(node);
		}
		return removed;
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
		try {
			Session session = getSession();
			Node rootNode = session.getRootNode();
			setLastModifiedDate(rootNode, new Date());
			for (NodeIterator iter = rootNode.getNodes(); iter.hasNext(); ) {
				Node node = iter.nextNode();
				try {
					if (!JcrConstants.JCR_SYSTEM.equals(node.getName())) {
						if (log.isDebugEnabled()) {
							log.debug("Removing node " + node.getPath());
						}
						node.remove();
					}
				}
				catch (javax.jcr.PathNotFoundException e) {
					// this is fine.
				}
			}
			RepositoryAdaptor<E> repositoryAdaptor = getRepositoryAdaptor();
			for (E element : repositoryAdaptor.getAllElements()) {
				addNodes(repositoryAdaptor.transform(element), false);
			}
			if (log.isDebugEnabled()) {
				showRepository();
			}
		}
		catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

	protected void showRepository() throws RepositoryException {
		log.debug("The following nodes are in the reposiory:");
		showRepository(getSession().getRootNode());
	}
	
	
	protected void showRepository(Node node) throws RepositoryException {
		log.debug("Node " + node.getPath());
		for (NodeIterator iter = node.getNodes(); iter.hasNext(); ) {
			showRepository(iter.nextNode());
		}
	}

	protected void addNodes(Collection<FileCommandBean> fileBeans, boolean removeExisting) throws RepositoryException {
		for (FileCommandBean fileBean : fileBeans) {
			addNode(fileBean, removeExisting);
		}
	}

	@Override
	public void add(int id) throws RepositoryException {
		add(id, false);
	}
	
	@Override
	public void addOrUpdate(int id) throws RepositoryException {
		add(id, true);
	}
	
	public void add(int id, boolean removeExisting) throws RepositoryException {
		E element = getRepositoryAdaptor().findById(id);
		if (element != null) {
			addNodes(getRepositoryAdaptor().transform(element), removeExisting);
		}
	}
	
	protected Node addNode(FileCommandBean fileBean, boolean removeExisting) throws RepositoryException {
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
		Node directoryNode = findOrCreateDirectory(directory);
		Node fileNode = directoryNode.addNode(name, JcrConstants.NT_FILE);
		Node node = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_UNSTRUCTURED);
		setLastModifiedDate(node, fileBean.getLastModifiedDate());
		String mimeType = fileBean.getMimeType();
		if (mimeType != null) {
			node.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
		}
		node.setProperty(PROPERTY_LENGTH, fileBean.getLength());
		node.setProperty(PROPERTY_ID, fileBean.getId());
		Lock lock = getLock();
		lock.lock();
		try {
			getSession().save();
		}
		finally {
			lock.unlock();
		}
		return fileNode;
	}
	
	protected void setLastModifiedDate(Node node, Date date) throws RepositoryException {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		node.setProperty(JcrConstants.JCR_LASTMODIFIED, calendar);		
	}
	
	protected Node findOrCreateDirectory(String directory) throws RepositoryException {
		if (directory.endsWith("/")) {
			directory = directory.substring(0, directory.length() - 1);
		}
		Node rootNode = getSession().getRootNode();
		if (directory.isEmpty()) {
			return rootNode;
		}
		try {
			return rootNode.getNode(directory);
		}
		catch (javax.jcr.PathNotFoundException e) {
			String parentDirectory = FilenameUtils.getPath(directory);
			String directoryName = FilenameUtils.getName(directory);
			Node parentNode = findOrCreateDirectory(parentDirectory);
			Node node = parentNode.addNode(directoryName, JcrConstants.NT_FOLDER);
			return node;
		}
	}

	@Override
	public void stream(Node node, KnownLengthOutputStream<?> out) throws RepositoryException, IOException {
		Property property = node.getProperty(PROPERTY_ID);
		int id = (int) property.getValue().getLong();
		getInputStreamCopier().copy(getDataExtractor(), id, out);
	}

	@Override
	public long getLength(Node node) throws RepositoryException {
		Property property = node.getProperty(PROPERTY_LENGTH);
		try {
			return property.getLong();
		}
		catch (ValueFormatException e) {
			throw new RepositoryException(e);
		}
	}
	
	public String getRepositoryRootPath() {
		return i_repositoryRootPath;
	}

	@Required
	public void setRepositoryRootPath(String repositoryRootPath) {
		i_repositoryRootPath = repositoryRootPath;
	}

	public Session getSession() {
		return i_session;
	}

	public void setSession(Session session) {
		i_session = session;
	}

	public RepositoryAdaptor<E> getRepositoryAdaptor() {
		return i_repositoryAdaptor;
	}

	@Required
	public void setRepositoryAdaptor(
			RepositoryAdaptor<E> pathInformationBeanAdaptor) {
		i_repositoryAdaptor = pathInformationBeanAdaptor;
	}

	public Repository getRepository() {
		return i_repository;
	}

	public void setRepository(Repository repository) {
		i_repository = repository;
	}

	public DataExtractor getDataExtractor() {
		return i_dataExtractor;
	}

	@Required
	public void setDataExtractor(DataExtractor dataExtractor) {
		i_dataExtractor = dataExtractor;
	}

	public String getBaseDirectory() {
		return i_baseDirectory;
	}

	@Required
	public void setBaseDirectory(String baseDirectory) {
		i_baseDirectory = baseDirectory;
	}

	public Lock getLock() {
		return i_lock;
	}

	public InputStreamCopier getInputStreamCopier() {
		return i_inputStreamCopier;
	}

	@Required
	public void setInputStreamCopier(InputStreamCopier inputStreamCopier) {
		i_inputStreamCopier = inputStreamCopier;
	}
}
