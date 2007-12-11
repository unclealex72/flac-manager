package uk.co.unclealex.music.core.initialise;


import java.io.IOException;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Initialiser {

	public void initialise() throws IOException;
	
	public void clear();	
}
