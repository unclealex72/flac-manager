package uk.co.unclealex.music.command;

import org.apache.commons.cli.CommandLine;
import org.springframework.context.ApplicationContext;

import uk.co.unclealex.music.encoding.EncodingService;

public class EncodeCommand extends SpringCommand {

	public static void main(String[] args) {
		new EncodeCommand().run(args);
	}

	@Override
	public void run(ApplicationContext ctxt, CommandLine commandLine) {
		EncodingService encodingService = ctxt.getBean(EncodingService.class);
		encodingService.encodeAll();
	}
}
