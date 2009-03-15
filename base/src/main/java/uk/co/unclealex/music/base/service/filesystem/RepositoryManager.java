package uk.co.unclealex.music.base.service.filesystem;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


public interface RepositoryManager {

	String PROPERTY_ID = "objectId";
	String PROPERTY_LENGTH = "length";
	
	public void updateFromScratch() throws RepositoryException;
	
	public int getObjectId(Node node);
	public long getLength(Node node);
	public void add(int id);
	public void addOrUpdate(int id);
}
