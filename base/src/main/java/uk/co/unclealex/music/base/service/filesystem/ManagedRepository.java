package uk.co.unclealex.music.base.service.filesystem;

import javax.jcr.Repository;

public interface ManagedRepository extends Repository {

	public RepositoryManager getRepositoryManager();
}
