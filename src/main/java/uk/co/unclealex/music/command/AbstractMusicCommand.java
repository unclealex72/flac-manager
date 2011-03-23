package uk.co.unclealex.music.command;

import com.google.inject.Module;

import uk.co.unclealex.executable.CommandLine;
import uk.co.unclealex.executable.GuiceCommand;
import uk.co.unclealex.music.inject.MusicModule;
import uk.co.unclealex.process.inject.ProcessServiceModule;


public abstract class AbstractMusicCommand<C extends CommandLine, S> extends GuiceCommand<C, S> {

	@Override
	protected Module[] getGuiceModules(C commandLine) {
		return new Module[] { new MusicModule(), new ProcessServiceModule() };
	}	
}