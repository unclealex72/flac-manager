package uk.co.unclealex.music.command;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.context.ApplicationContext;

import uk.co.unclealex.music.encoding.RenamingService;

public class MergeCommand extends SpringCommand implements Transformer<String, File> {

	private static final String ALBUM_OPTION = "album";
	private static final String DIRS_OPTION = "dirs";
	
	public static void main(String[] args) {
		new MergeCommand().run(args);
	}

	@Override
	public File transform(String path) {
		return new File(path);
	}
	
	@SuppressWarnings("static-access")
	@Override
	protected Option[] addOptions() {
		Option albumOption = 
			OptionBuilder.
				hasArg().
				withArgName("album name").
				withDescription("The new album name.").
				isRequired().
				create(ALBUM_OPTION);
		Option dirsOption = 
			OptionBuilder.
				hasArgs().
				withArgName("dirs").
				withDescription("The directories from which to take flac files.").
				isRequired().
				create(DIRS_OPTION);
		return new Option[] { albumOption, dirsOption };
	}
		
	@Override
	public void run(ApplicationContext ctxt, CommandLine commandLine) throws IOException {
		Set<File> flacDirectories  = 
			CollectionUtils.collect(
				Arrays.asList(commandLine.getOptionValues(DIRS_OPTION)), this, new LinkedHashSet<File>());
		String album = commandLine.getOptionValue(ALBUM_OPTION);
		RenamingService renamingService = ctxt.getBean(RenamingService.class);
		renamingService.merge(album, flacDirectories);
	}
}
