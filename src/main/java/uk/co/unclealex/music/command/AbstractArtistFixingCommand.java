package uk.co.unclealex.music.command;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import uk.co.unclealex.music.ArtistFixingService;

public abstract class AbstractArtistFixingCommand extends SpringCommand<ArtistFixingService> {

	protected static final String CACHE_OPTION = "cache";

	@Override
	protected Options createOptions() {
		Options options = super.createOptions();
		@SuppressWarnings("static-access")
		Option cacheOption = 
			OptionBuilder.
				hasArg().
				withArgName("cache file").
				withDescription("The cache file for artists and their tracks.").
				isRequired().
				create(CACHE_OPTION);
		options.addOption(cacheOption);
		return options;
	}
	
	@Override
	protected void run(ArtistFixingService service, CommandLine commandLine) throws Exception {
		run(service, new File(commandLine.getOptionValue(CACHE_OPTION)), commandLine);
	}
	
	protected abstract void run(ArtistFixingService service, File cacheFile, CommandLine commandLine) throws Exception;

	@Override
	protected Class<? extends ArtistFixingService> getServiceClass() {
		return ArtistFixingService.class;
	}
}
