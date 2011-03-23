package uk.co.unclealex.music.command;

import java.util.List;
import java.util.Map;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.unclealex.music.ArtistFixingService;

public class FixArtistsCommand extends AbstractArtistFixingCommand<FixArtistCommandLine> {

	@Override
	protected void run(ArtistFixingService service, FixArtistCommandLine commandLine) throws Exception {
		Map<String, String> newArtistNamesByOriginalArtistName = createMapFromListArguments(commandLine.getFixes());
		service.fixArtists(newArtistNamesByOriginalArtistName, commandLine.getCache());
	}
}

@CommandLineInterface(application="flac-fix-artists")
interface FixArtistCommandLine extends AbstractArtistFixingCommandLine {
	
	@Option(shortName="f", pattern=".+?=.+", description="A list of values of the form \"old artist=new artist\"")
	public List<String> getFixes();
}