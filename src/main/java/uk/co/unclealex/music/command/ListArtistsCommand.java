package uk.co.unclealex.music.command;

import java.io.File;
import java.io.PrintWriter;
import java.util.SortedSet;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Unparsed;
import uk.co.unclealex.music.ArtistFixingService;

public class ListArtistsCommand extends AbstractArtistFixingCommand<ListArtistsCommandLine> {


	@Override
	protected void run(ArtistFixingService service, ListArtistsCommandLine commandLine) throws Exception {
		File outFile = commandLine.getOutputFile();
		SortedSet<String> artists = service.listArtists(commandLine.getCache());
		PrintWriter writer = new PrintWriter(outFile);
		for (String artist : artists) {
			writer.println(artist);
		}
		writer.close();
	}
}

@CommandLineInterface(application="flac-list-artists")
interface ListArtistsCommandLine extends AbstractArtistFixingCommandLine {
	
	@Unparsed(name="output file")
	public File getOutputFile();
}