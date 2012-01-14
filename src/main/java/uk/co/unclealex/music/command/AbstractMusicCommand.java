package uk.co.unclealex.music.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import uk.co.unclealex.executable.CommandLine;
import uk.co.unclealex.executable.GuiceCommand;
import uk.co.unclealex.music.inject.MusicModule;
import uk.co.unclealex.process.inject.ProcessServiceModule;

import com.google.inject.Module;


public abstract class AbstractMusicCommand<C extends CommandLine, S> extends GuiceCommand<C, S> {

	protected Set<File> canonicalise(Set<File> result, List<File> nonCanonicalFiles, boolean expectDirectories) throws IOException {
		for (File file : nonCanonicalFiles) {
			if (!file.canRead()) {
				throw new FileNotFoundException(file.getPath());
			}
			if (expectDirectories && !file.isDirectory()) {
				throw new IOException("File " + file + " is not a directory.");
			}
			else if (!expectDirectories && !file.isFile()) {
				throw new IOException("File " + file + " is a directory.");
			}
			result.add(file.getCanonicalFile());
		}
		return result;
	}
	
	@Override
	protected Module[] getGuiceModules(C commandLine) {
		return new Module[] { new MusicModule(), new ProcessServiceModule() };
	}	
}