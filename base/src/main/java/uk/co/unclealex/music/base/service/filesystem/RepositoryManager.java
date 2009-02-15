package uk.co.unclealex.music.base.service.filesystem;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Repository;


public interface RepositoryManager {

	String PROPERTY_ID = "objectId";
	String PROPERTY_LENGTH = "length";
	
	public Repository getRepository();
	
	public void refresh();
	public void clear();
	
	public int getObjectId(Node node);
	public long getLength(Node node);
	public void add(int id);
	public int remove(int id);
	public Set<Node> find(int id);
	public void addOrUpdate(int id);
}
