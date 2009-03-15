package uk.co.unclealex.music.test;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.core.RepositoryImpl;

import uk.co.unclealex.music.base.service.filesystem.ClearableRepositoryFactory;

public class TestRepositoryFactory implements ClearableRepositoryFactory {

	@Override
	public void clearNextInstance() {
	}

	@Override
	public RepositoryImpl getRepository() throws RepositoryException {
		return null;
	}

}
