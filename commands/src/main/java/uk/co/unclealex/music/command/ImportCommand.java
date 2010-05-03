package uk.co.unclealex.music.command;
import java.io.File;

import org.springframework.context.ApplicationContext;

import uk.co.unclealex.music.encoding.ImportService;

public class ImportCommand extends SpringCommand {

	public static void main(String[] args) {
		new ImportCommand().run(args);
	}

	@Override
	public void run(ApplicationContext ctxt, String[] args) {
		ImportService importService = ctxt.getBean(ImportService.class);
		importService.importFromDirectory(new File("/mnt/home/converted"));
	}
}
