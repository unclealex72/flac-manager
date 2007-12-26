package uk.co.unclealex.music.commands;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.core.initialise.Initialiser;
import uk.co.unclealex.music.core.mains.Main;
import uk.co.unclealex.music.encoder.initialise.Importer;
import uk.co.unclealex.music.encoder.service.EncoderService;

public class Initialise extends EncoderMain {

	private Importer i_importer;
	private Initialiser i_initialiser;
	private EncoderService i_encoderService;
	
	@Override
	public void execute() throws Exception {
		getInitialiser().clear();
		getInitialiser().initialise();
		getImporter().importTracks();
		EncoderService encoderService = getEncoderService();
		encoderService.encodeAll(8);
		encoderService.removeDeleted();
	}
	
	public static void main(String[] args) throws Exception {
		Main.execute(new Initialise());
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

}
