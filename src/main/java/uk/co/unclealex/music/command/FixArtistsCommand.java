package uk.co.unclealex.music.command;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import uk.co.unclealex.music.ArtistFixingService;

public class FixArtistsCommand extends AbstractArtistFixingCommand {

	private static final String FIX_OPTION = "fix";

	public static void main(String[] args) {
		new FixArtistsCommand().run(args);
	}

	@Override
	protected Option[] addOptions() {
		@SuppressWarnings("static-access")
		Option fixOption  = 
			OptionBuilder.
				withArgName("old artist=new artist").
          hasArgs(2).
          withValueSeparator().
          withDescription("Rename the old artist to the new artist").
          create(FIX_OPTION);
		return new Option[] { fixOption };
	}

	@Override
	protected void run(ArtistFixingService service, File cacheFile, CommandLine commandLine) throws Exception {
		Map<String, String> newArtistNamesByOriginalArtistName = new LinkedHashMap<String, String>();
		String[] fixValues = commandLine.getOptionValues(FIX_OPTION);
		for (int idx = 0; idx < fixValues.length; idx += 2) {
			String oldArtistName = fixValues[idx].trim();
			String newArtistName = fixValues[idx+1].trim();
			newArtistNamesByOriginalArtistName.put(oldArtistName, newArtistName);
		}
		service.fixArtists(newArtistNamesByOriginalArtistName, cacheFile);
	}
}
