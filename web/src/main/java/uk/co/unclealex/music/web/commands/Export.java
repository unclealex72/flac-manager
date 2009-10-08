package uk.co.unclealex.music.web.commands;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.commands.Command;
import uk.co.unclealex.music.encoder.initialise.Importer;

@Transactional(rollbackFor=Exception.class)
public class Export implements Command {

	private Importer i_importer;
	
	@Override
	public void execute(String[] args) throws IOException {
		getImporter().exportTracks();
	}
	
	public Importer getImporter() {
		return i_importer;
	}

	@Required
	public void setImporter(Importer importer) {
		i_importer = importer;
	}

}
