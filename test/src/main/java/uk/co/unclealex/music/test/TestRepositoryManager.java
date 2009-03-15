package uk.co.unclealex.music.test;

import javax.jcr.Node;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;

@Transactional
public class TestRepositoryManager implements RepositoryManager {

	@Override
	public void add(int id) {
	}

	@Override
	public void addOrUpdate(int id) {
	}

	@Override
	public long getLength(Node node) {
		return 0;
	}

	@Override
	public int getObjectId(Node node) {
		return 0;
	}

	@Override
	public void updateFromScratch() {
	}
}
