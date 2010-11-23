package uk.co.unclealex.music.command;

import org.apache.commons.cli.CommandLine;

import uk.co.unclealex.music.encoding.EncodingService;

public class EncodeCommand extends AbstractEncodingCommand {

	public static void main(String[] args) {
		new EncodeCommand().run(args);
	}

	@Override
	public void run(EncodingService encodingService, CommandLine commandLine) {
		encodingService.encodeAll();
	}	
}
