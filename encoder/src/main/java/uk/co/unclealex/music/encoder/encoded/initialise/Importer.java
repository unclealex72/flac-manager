package uk.co.unclealex.music.encoder.encoded.initialise;


import java.io.IOException;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Importer {

	public void initialise() throws IOException;
	
	public void importTracks() throws IOException;

	public void clear();	
}
