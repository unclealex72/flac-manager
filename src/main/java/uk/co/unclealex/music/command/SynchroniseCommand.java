package uk.co.unclealex.music.command;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;

import uk.co.unclealex.music.sync.SynchroniserService;

public class SynchroniseCommand extends SpringCommand<SynchroniserService> {

	public static void main(String[] args) {
		new SynchroniseCommand().run(args);
	}

	@Override
	public void run(SynchroniserService synchroniserService, CommandLine commandLine) throws IOException {
		if (commandLine.getArgs().length == 0) {
			synchroniserService.synchroniseAll();
		}
		else {
			synchroniserService.synchronise(commandLine.getArgs()[0]);
		}
	}
	
	@Override
	protected Class<? extends SynchroniserService> getServiceClass() {
		return SynchroniserService.class;
	}
}
