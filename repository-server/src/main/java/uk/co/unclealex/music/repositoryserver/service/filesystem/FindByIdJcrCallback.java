package uk.co.unclealex.music.repositoryserver.service.filesystem;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.JcrConstants;
import org.springmodules.jcr.JcrCallback;

import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;

public abstract class FindByIdJcrCallback<V> implements JcrCallback<V> {

	private int i_id;

	public FindByIdJcrCallback(int id) {
		super();
		i_id = id;
	}

	public Set<Node> findById(Session session) throws RepositoryException {
		Set<Node> foundNodes = new HashSet<Node>();
		Query query = session.getWorkspace().getQueryManager().createQuery(
				"//" + JcrConstants.JCR_CONTENT + "[@" + RepositoryManager.PROPERTY_ID + "='" + getId() + "']", Query.XPATH);
		QueryResult queryResult = query.execute();
		for (NodeIterator iter = queryResult.getNodes(); iter.hasNext(); ) {
			Node node = iter.nextNode();
			foundNodes.add(node.getParent());
		}
		return foundNodes;
	}

	public int getId() {
		return i_id;
	}
	
}
