package uk.co.unclealex.music.legacy.command;
import java.io.File;

import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Unparsed;
import uk.co.unclealex.executable.CommandLine;
import uk.co.unclealex.music.legacy.encoding.ImportService;

public class ImportCommand extends AbstractMusicCommand<ImportCommandLine, ImportService> {

	@Override
	public void run(ImportService importService, ImportCommandLine commandLine) {
		importService.importFromDirectory(new File("/mnt/home/converted"));
	}
	
	@Override
	protected Class<? extends ImportService> getServiceClass() {
		return ImportService.class;
	}
}

@CommandLineInterface(application="flac-import")
interface ImportCommandLine extends CommandLine {
	
	@Unparsed(name="import directory")
	public File getImportDirectory();
	
	public boolean isImportDirectory();
}