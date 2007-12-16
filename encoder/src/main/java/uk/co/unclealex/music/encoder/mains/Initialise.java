package uk.co.unclealex.music.encoder.mains;

import uk.co.unclealex.music.core.mains.Main;
import uk.co.unclealex.music.encoder.encoded.initialise.Importer;
import uk.co.unclealex.music.encoder.encoded.service.EncoderService;

@uk.co.unclealex.music.core.spring.Main
public class Initialise extends Main {

	private Importer i_importer;
	private EncoderService i_encoderService;
	
	@Override
	public void execute() throws Exception {
		//Importer initialiser = getInitialiser();
		//initialiser.initialise();
		//initialiser.importTracks();
		EncoderService encoderService = getEncoderService();
		encoderService.encodeAll(8);
		encoderService.removeDeleted();
	}
	
	public static void main(String[] args) throws Exception {
		Main.execute(new Initialise());
	}

	public Importer getInitialiser() {
		return i_importer;
	}

	public void setInitialiser(Importer importer) {
		i_importer = importer;
	}

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

}
