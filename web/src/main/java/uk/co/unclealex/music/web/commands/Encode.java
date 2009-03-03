package uk.co.unclealex.music.web.commands;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.commands.Command;
import uk.co.unclealex.music.encoder.service.AlreadyEncodingException;
import uk.co.unclealex.music.encoder.service.CurrentlyScanningException;
import uk.co.unclealex.music.encoder.service.EncoderService;
import uk.co.unclealex.music.encoder.service.MultipleEncodingException;

@Service
@Transactional(rollbackFor=Exception.class)
public class Encode implements Command {

	private EncoderService i_encoderService;
	
	@Override
	public void execute(String[] args) throws AlreadyEncodingException, MultipleEncodingException, CurrentlyScanningException, IOException {
		getEncoderService().encodeAllAndRemoveDeleted();
	}
	
	public EncoderService getEncoderService() {
		return i_encoderService;
	}

	@Required
	public void setEncoderService(EncoderService encoderService) {
		i_encoderService = encoderService;
	}
}
