package uk.co.unclealex.music.test;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;

@Transactional
public class TestRepositoryManager implements RepositoryManager {

	@Override
	public void add(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addOrUpdate(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Node> find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLength(Node node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getObjectId(Node node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Repository getRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int remove(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Session getSession() {
		// TODO Auto-generated method stub
		return null;
	}

}
