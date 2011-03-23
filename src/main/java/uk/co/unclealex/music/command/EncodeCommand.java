package uk.co.unclealex.music.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.unclealex.executable.CommandLine;
import uk.co.unclealex.music.encoding.EncodingService;

public class EncodeCommand extends AbstractEncodingCommand<EncodeCommandLine> {

	@Override
	public void run(EncodingService encodingService, EncodeCommandLine commandLine) throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("logging.properties");
		LogManager.getLogManager().readConfiguration(in);
		in.close();
		encodingService.encodeAll();
	}	
}

@CommandLineInterface(application="flac-encode")
interface EncodeCommandLine extends CommandLine {
	// Marker interface
}