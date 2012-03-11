package uk.co.unclealex.music.legacy.command;
import java.io.IOException;
import java.util.ArrayList;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Unparsed;
import uk.co.unclealex.executable.CommandLine;
import uk.co.unclealex.music.legacy.sync.SynchroniserService;

public class SynchroniseCommand extends AbstractMusicCommand<SynchroniseCommandLine, SynchroniserService> {

	public static void main(String[] args) {
		new SynchroniseCommand().execute(new ArrayList<String>());
	}
	
	@Override
	protected void run(SynchroniserService synchroniserService, SynchroniseCommandLine commandLine) throws IOException {
		if (!commandLine.isDeviceName()) {
			synchroniserService.synchroniseAll();
		}
		else {
			synchroniserService.synchronise(commandLine.getDeviceName());
		}
	}
	
	@Override
	protected Class<? extends SynchroniserService> getServiceClass() {
		return SynchroniserService.class;
	}
}

@CommandLineInterface(application="flac-sync")
interface SynchroniseCommandLine extends CommandLine {
	
	@Unparsed(name="device name")
	public String getDeviceName();
	
	public boolean isDeviceName();
}