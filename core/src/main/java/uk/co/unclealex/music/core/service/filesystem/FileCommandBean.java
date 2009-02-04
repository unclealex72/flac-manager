package uk.co.unclealex.music.core.service.filesystem;

import java.util.Date;

import uk.co.unclealex.music.core.service.CommandBean;

public class FileCommandBean extends CommandBean<FileCommandBean> {

	private int i_id;
	private String i_path;
	private Date i_lastModifiedDate;
	private long i_length;
	private String i_mimeType;

	public FileCommandBean() {
		// Default constructor to be used as the end of work marker.
	}
	
	public FileCommandBean(int id, String path, Date lastModifiedDate, long length, String mimeType) {
		super();
		i_id = id;
		i_path = path;
		i_lastModifiedDate = lastModifiedDate;
		i_length = length;
		i_mimeType = mimeType;
	}

	@Override
	public boolean isEndOfWorkBean() {
		return getPath() == null;
	}
	
	@Override
	public int compareTo(FileCommandBean o) {
		return getPath().compareTo(o.getPath());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof FileCommandBean && getPath().equals(((FileCommandBean) obj).getPath());
	}
	
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}
	
	public int getId() {
		return i_id;
	}

	public String getPath() {
		return i_path;
	}

	public Date getLastModifiedDate() {
		return i_lastModifiedDate;
	}

	public long getLength() {
		return i_length;
	}
	
	public String getMimeType() {
		return i_mimeType;
	}
}
