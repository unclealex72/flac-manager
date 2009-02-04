package uk.co.unclealex.music.commands;

import org.springframework.beans.factory.annotation.Required;

import uk.co.unclealex.music.encoder.initialise.Importer;

public class Export extends Main {

	private Importer i_importer;
	
	@Override
	public void execute() throws Exception {
		getImporter().exportTracks();
	}
	
	public static void main(String[] args) throws Exception {
		Main.execute(new Export());
	}

	public Importer getImporter() {
		return i_importer;
	}

	@Required
	public void setImporter(Importer importer) {
		i_importer = importer;
	}

}
