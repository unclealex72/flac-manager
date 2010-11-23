package uk.co.unclealex.music.command;
import java.io.File;

import org.apache.commons.cli.CommandLine;

import uk.co.unclealex.music.encoding.ImportService;

public class ImportCommand extends SpringCommand<ImportService> {

	public static void main(String[] args) {
		new ImportCommand().run(args);
	}

	@Override
	public void run(ImportService importService, CommandLine commandLine) {
		importService.importFromDirectory(new File("/mnt/home/converted"));
	}
	
	@Override
	protected Class<? extends ImportService> getServiceClass() {
		return ImportService.class;
	}
}
