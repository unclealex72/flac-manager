package uk.co.unclealex.music.repositoryserver.service.filesystem;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.jackrabbit.core.query.ExecutableQuery;
import org.apache.jackrabbit.spi.Name;

public class EmptyQuery implements ExecutableQuery, QueryResult, NodeIterator, RowIterator {

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
}
