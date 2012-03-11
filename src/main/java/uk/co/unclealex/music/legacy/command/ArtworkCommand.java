package uk.co.unclealex.music.legacy.command;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.unclealex.executable.CommandLine;
import uk.co.unclealex.music.legacy.encoding.EncodingService;

public class ArtworkCommand extends AbstractEncodingCommand<ArtworkCommandLine> {

	@Override
	public void run(EncodingService encodingService, ArtworkCommandLine commandLine) {
		encodingService.refreshArtwork();
	}	
}

@CommandLineInterface(application="flac-artwork")
interface ArtworkCommandLine extends CommandLine {
	// Marker interface
}