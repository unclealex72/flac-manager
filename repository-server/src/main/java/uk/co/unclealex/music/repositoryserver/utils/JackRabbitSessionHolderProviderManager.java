package uk.co.unclealex.music.repositoryserver.utils;

import javax.jcr.Repository;

import org.springmodules.jcr.SessionHolderProvider;
import org.springmodules.jcr.SessionHolderProviderManager;
import org.springmodules.jcr.jackrabbit.support.JackRabbitSessionHolderProvider;

public class JackRabbitSessionHolderProviderManager implements SessionHolderProviderManager {

	@Override
	public SessionHolderProvider getSessionProvider(Repository repository) {
		return new JackRabbitSessionHolderProvider();
	}

}
