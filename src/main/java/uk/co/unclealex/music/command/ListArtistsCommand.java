package uk.co.unclealex.music.command;

import java.io.File;
import java.io.PrintWriter;
import java.util.SortedSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import uk.co.unclealex.music.ArtistFixingService;

public class ListArtistsCommand extends AbstractArtistFixingCommand {

	private static final String OUT_OPTION = "out";

	public static void main(String[] args) {
		new ListArtistsCommand().run(args);
	}

	@Override
	protected Option[] addOptions() {
		@SuppressWarnings("static-access")
		Option outOption = 
			OptionBuilder.
				hasArg().
				withArgName("output file").
				withDescription("The file in which to list all artists.").
				isRequired().
				create(OUT_OPTION);
		return new Option[] { outOption };
	}

	@Override
	protected void run(ArtistFixingService service, File cacheFile, CommandLine commandLine) throws Exception {
		File outFile = new File(commandLine.getOptionValue(OUT_OPTION));
		SortedSet<String> artists = service.listArtists(cacheFile);
		PrintWriter writer = new PrintWriter(outFile);
		for (String artist : artists) {
			writer.println(artist);
		}
		writer.close();
	}
}
