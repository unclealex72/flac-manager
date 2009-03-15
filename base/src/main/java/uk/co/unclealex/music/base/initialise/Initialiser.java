package uk.co.unclealex.music.base.initialise;


import java.io.IOException;

import javax.jcr.RepositoryException;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Initialiser {

	public void initialise() throws IOException, RepositoryException;
	public void clear() throws RepositoryException;
}
