package uk.co.unclealex.music.base.service.filesystem;

import org.apache.jackrabbit.core.TransientRepository.RepositoryFactory;

public interface ClearableRepositoryFactory extends RepositoryFactory {

	public void clearNextInstance();
}
