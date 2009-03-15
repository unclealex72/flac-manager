package uk.co.unclealex.music.repositoryserver.service.filesystem;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.jackrabbit.core.ItemManager;
import org.apache.jackrabbit.core.NodeId;
import org.apache.jackrabbit.core.NodeIdIterator;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.query.ExecutableQuery;
import org.apache.jackrabbit.core.query.QueryHandler;
import org.apache.jackrabbit.core.query.QueryHandlerContext;
import org.apache.jackrabbit.core.state.NodeState;
import org.apache.jackrabbit.core.state.NodeStateIterator;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.query.qom.QueryObjectModelTree;

public class EmptyQueryHandler implements QueryHandler, ExecutableQuery, QueryResult, NodeIterator, RowIterator {

	private QueryHandlerContext i_context;
	
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
		return this;
	}

	@Override
	public ExecutableQuery createExecutableQuery(SessionImpl session, ItemManager itemMgr, String statement,
			String language) throws InvalidQueryException {
		return this;
	}

	@Override
	public void deleteNode(NodeId id) throws IOException {
		// Do nothing
	}

	@Override
	public void updateNodes(NodeIdIterator remove, NodeStateIterator add) throws RepositoryException, IOException {
		// Do nothing
	}

	@Override
	public void bindValue(Name varName, Value value) throws IllegalArgumentException, RepositoryException {
		// Do nothing
	}

	@Override
	public QueryResult execute(long offset, long limit) throws RepositoryException {
		return this;
	}

	@Override
	public String[] getColumnNames() {
		return new String[0];
	}

	@Override
	public NodeIterator getNodes() {
		return this;
	}

	@Override
	public RowIterator getRows() {
		return this;
	}

	@Override
	public Node nextNode() {
		return null;
	}

	@Override
	public long getPosition() {
		return 0;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public void skip(long skipNum) {
		// Do nothing
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public Object next() {
		return null;
	}

	@Override
	public void remove() {
		// Do nothing
	}

	@Override
	public Row nextRow() {
		return null;
	}

	public QueryHandlerContext getContext() {
		return i_context;
	}

	public void setContext(QueryHandlerContext context) {
		i_context = context;
	}

}
