package uk.co.unclealex.music.encoder.initialise;


import java.io.IOException;

import javax.jcr.RepositoryException;

public interface Importer {

	public void importTracks() throws IOException, RepositoryException;

	public void exportTracks() throws IOException;
}
