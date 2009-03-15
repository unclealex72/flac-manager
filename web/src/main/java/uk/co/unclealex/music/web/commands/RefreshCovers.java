package uk.co.unclealex.music.web.commands;

import java.io.IOException;

import javax.jcr.RepositoryException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import uk.co.unclealex.music.base.service.filesystem.ClearableRepositoryFactory;
import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;
import uk.co.unclealex.music.commands.Command;

@Service
public class RefreshCovers implements Command {

	private RepositoryManager i_coversRepositoryManager;
	private ClearableRepositoryFactory i_coversRepositoryFactory;
	
	@Override
	public void execute(String[] args) throws IOException, RepositoryException {
		getCoversRepositoryFactory().clearNextInstance();
		getCoversRepositoryManager().updateFromScratch();
	}

	public RepositoryManager getCoversRepositoryManager() {
		return i_coversRepositoryManager;
	}

	@Required
	public void setCoversRepositoryManager(RepositoryManager coversRepositoryManager) {
		i_coversRepositoryManager = coversRepositoryManager;
	}

	public ClearableRepositoryFactory getCoversRepositoryFactory() {
		return i_coversRepositoryFactory;
	}

	@Required
	public void setCoversRepositoryFactory(ClearableRepositoryFactory coversRepositoryFactory) {
		i_coversRepositoryFactory = coversRepositoryFactory;
	}	
}
