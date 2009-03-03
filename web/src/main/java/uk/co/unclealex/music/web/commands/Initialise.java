package uk.co.unclealex.music.web.commands;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.initialise.Initialiser;
import uk.co.unclealex.music.base.service.filesystem.RepositoryManager;
import uk.co.unclealex.music.commands.Command;
import uk.co.unclealex.music.encoder.initialise.Importer;
import uk.co.unclealex.music.encoder.service.AlreadyEncodingException;
import uk.co.unclealex.music.encoder.service.CurrentlyScanningException;
import uk.co.unclealex.music.encoder.service.EncoderService;
import uk.co.unclealex.music.encoder.service.MultipleEncodingException;

@Service
@Transactional(rollbackFor=Exception.class)
public class Initialise implements Command {

	private Importer i_importer;
	private Initialiser i_initialiser;
	private EncoderService i_encoderService;
	private RepositoryManager i_repositoryManager;
	
	@Override
	public void execute(String[] args) throws IOException, AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException {
		getInitialiser().clear();
		getInitialiser().initialise();
		getImporter().importTracks();
		EncoderService encoderService = getEncoderService();
		encoderService.encodeAll(1);
		encoderService.removeDeleted();
	}
	
	public Importer getImporter() {
		return i_importer;
	}

	@Required
	public void setImporter(Importer importer) {
		i_importer = importer;
	}

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	@Required
	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

	public Initialiser getInitialiser() {
		return i_initialiser;
	}

	@Required
	public void setInitialiser(Initialiser initialiser) {
		i_initialiser = initialiser;
	}

	public RepositoryManager getRepositoryManager() {
		return i_repositoryManager;
	}

	public void setRepositoryManager(RepositoryManager repositoryManager) {
		i_repositoryManager = repositoryManager;
	}

}
