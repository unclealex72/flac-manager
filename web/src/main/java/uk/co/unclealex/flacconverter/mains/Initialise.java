package uk.co.unclealex.flacconverter.mains;

import uk.co.unclealex.flacconverter.encoded.initialise.Initialiser;
import uk.co.unclealex.flacconverter.encoded.service.EncoderService;

public class Initialise extends Main {

	private Initialiser i_initialiser;
	private EncoderService i_encoderService;
	
	@Override
	public void execute() throws Exception {
		//Initialiser initialiser = getInitialiser();
		//initialiser.initialise();
		//initialiser.importTracks();
		EncoderService encoderService = getEncoderService();
		encoderService.encodeAll(8);
		encoderService.removeDeleted();
	}
	
	public static void main(String[] args) throws Exception {
		Main.execute(new Initialise());
	}

	public Initialiser getInitialiser() {
		return i_initialiser;
	}

	public void setInitialiser(Initialiser initialiser) {
		i_initialiser = initialiser;
	}

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

}
