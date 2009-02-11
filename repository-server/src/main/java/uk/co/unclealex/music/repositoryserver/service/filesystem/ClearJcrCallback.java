package uk.co.unclealex.music.repositoryserver.service.filesystem;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.JcrConstants;
import org.apache.log4j.Logger;
import org.springmodules.jcr.JcrCallback;

public abstract class ClearJcrCallback<V> implements JcrCallback<V> {

	private static final Logger log = Logger.getLogger(ClearJcrCallback.class);
	
	public void clear(Session session) throws RepositoryException {
		Node rootNode = session.getRootNode();
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
	}
}
