package uk.co.unclealex.music.repositoryserver.service.filesystem;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;

import org.apache.jackrabbit.core.ItemManager;
import org.apache.jackrabbit.core.NodeId;
import org.apache.jackrabbit.core.NodeIdIterator;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.query.ExecutableQuery;
import org.apache.jackrabbit.core.query.QueryHandler;
import org.apache.jackrabbit.core.query.QueryHandlerContext;
import org.apache.jackrabbit.core.state.NodeState;
import org.apache.jackrabbit.core.state.NodeStateIterator;
import org.apache.jackrabbit.spi.commons.query.qom.QueryObjectModelTree;

public class EmptyQueryHandler implements QueryHandler {

	private QueryHandlerContext i_context;
	
	@Override
	public String getQueryClass() {
		return EmptyQuery.class.getName();
	}

	@Override
	public void init(QueryHandlerContext context) throws IOException {
		setContext(context);
	}

	@Override
	public void addNode(NodeState node) throws RepositoryException, IOException {
		// Do nothing
	}

	@Override
	public void close() throws IOException {
		// Do nothing
	}

	@Override
	public ExecutableQuery createExecutableQuery(SessionImpl session, ItemManager itemMgr, QueryObjectModelTree qomTree)
			throws InvalidQueryException {
		return new EmptyQuery();
	}

	@Override
	public ExecutableQuery createExecutableQuery(SessionImpl session, ItemManager itemMgr, String statement,
			String language) throws InvalidQueryException {
		return new EmptyQuery();
	}

	@Override
	public void deleteNode(NodeId id) throws IOException {
		// Do nothing
	}

	@Override
	public void updateNodes(NodeIdIterator remove, NodeStateIterator add) throws RepositoryException, IOException {
		// Do nothing
	}

	public QueryHandlerContext getContext() {
		return i_context;
	}

	public void setContext(QueryHandlerContext context) {
		i_context = context;
	}

}
