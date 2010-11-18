package uk.co.unclealex.music.command;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.springframework.context.ApplicationContext;

import uk.co.unclealex.music.sync.SynchroniserService;

public class SynchroniseCommand extends SpringCommand {

	public static void main(String[] args) {
		new SynchroniseCommand().run(args);
	}

	@Override
	protected void checkCommandLine(CommandLine commandLine) throws ParseException {
		if (commandLine.getArgs().length == 0) {
			throw new ParseException("You must supply a device name.");
		}
	}
	
	@Override
	public void run(ApplicationContext ctxt, CommandLine commandLine) throws IOException {
		SynchroniserService synchroniserService = ctxt.getBean(SynchroniserService.class);
		synchroniserService.synchronise(commandLine.getArgs()[0]);
	}
}