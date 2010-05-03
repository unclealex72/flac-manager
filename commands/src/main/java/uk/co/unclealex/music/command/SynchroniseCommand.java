package uk.co.unclealex.music.command;
import java.io.IOException;

import org.springframework.context.ApplicationContext;

import uk.co.unclealex.music.sync.SynchroniserService;

public class SynchroniseCommand extends SpringCommand {

	public static void main(String[] args) {
		new SynchroniseCommand().run(args);
	}

	@Override
	public void run(ApplicationContext ctxt, String[] args) throws IOException {
		SynchroniserService synchroniserService = ctxt.getBean(SynchroniserService.class);
		synchroniserService.synchronise(args[0]);
	}
}
