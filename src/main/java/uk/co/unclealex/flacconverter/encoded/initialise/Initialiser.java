package uk.co.unclealex.flacconverter.encoded.service;


import java.io.IOException;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Initialiser {

	public void initialise();
	
	public void importTracks() throws IOException;

	public void clear();
}
