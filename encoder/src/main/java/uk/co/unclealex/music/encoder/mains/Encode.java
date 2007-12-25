package uk.co.unclealex.music.encoder.mains;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.core.mains.Main;
import uk.co.unclealex.music.core.service.EncodedService;
import uk.co.unclealex.music.encoder.service.EncoderService;

public class Encode extends EncoderMain {

	private EncoderService i_encoderService;
	private EncodedService i_encodedService;
	
	@Override
	public void execute() throws Exception {
		getEncoderService().encodeAllAndRemoveDeleted();
		getEncodedService().updateAllFilenames();
	}
	
	public static void main(String[] args) throws Exception {
		Main.execute(new Encode());
	}

	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	@Required
	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}

	public EncodedService getEncodedService() {
		return i_encodedService;
	}

	@Required
	public void setEncodedService(EncodedService encodedService) {
		i_encodedService = encodedService;
	}
}
