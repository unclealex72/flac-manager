package uk.co.unclealex.music.command;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;
import uk.co.unclealex.executable.CommandLine;
import uk.co.unclealex.music.encoding.RenamingService;

public class MergeCommand extends AbstractRenamingCommand<MergeCommandLine> {

	@Override
	public void run(RenamingService renamingService, MergeCommandLine commandLine) throws IOException {
		Set<File> flacDirectories  = canonicalise(new LinkedHashSet<File>(), commandLine.getDirectories(), true); 
		String album = commandLine.getAlbum();
		renamingService.merge(album, flacDirectories);
	}
}

@CommandLineInterface(application="flac-merge")
interface MergeCommandLine extends CommandLine {
	
	@Option(shortName="a", description = "The new album name")
	public String getAlbum();
	
	@Unparsed(name = "directories")
	public List<File> getDirectories();
}