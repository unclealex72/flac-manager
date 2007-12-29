package uk.co.unclealex.music.core.service.filesystem;

import java.util.Date;

public abstract class PathInformationBean implements Comparable<PathInformationBean> {

	private Date i_creationDate;
	private Date i_lastModifiedDate;
	private String i_path;
	private long i_length;
	
	public PathInformationBean(String path, long length) {
		super();
		i_path = path;
		i_length = length;
	}

	public abstract void accept(PathInformationBeanVisitor visitor);
	
	public int compareTo(PathInformationBean o) {
		return getPath().compareTo(o.getPath());
	}
	
	public Date getCreationDate() {
		return i_creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		i_creationDate = creationDate;
	}
	
	public Date getLastModifiedDate() {
		return i_lastModifiedDate;
	}
	
	public void setLastModifiedDate(Date lastModifiedDate) {
		i_lastModifiedDate = lastModifiedDate;
	}

	public String getPath() {
		return i_path;
	}

	protected void setPath(String path) {
		i_path = path;
	}

	public long getLength() {
		return i_length;
	}

	protected void setLength(long length) {
		i_length = length;
	}
	
	
}
