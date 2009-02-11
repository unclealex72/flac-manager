package uk.co.unclealex.music.repositoryserver.service.filesystem;

import java.util.Set;

import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.music.base.service.filesystem.FileCommandBean;

public interface RepositoryAdaptor<E> extends Transformer<E, Set<FileCommandBean>>{

	public Set<E> getAllElements();
	
	public E findById(int id);
}
