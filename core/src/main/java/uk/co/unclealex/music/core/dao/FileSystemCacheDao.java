package uk.co.unclealex.music.core.dao;

public interface FileSystemCacheDao {

	public boolean isRebuildRequired();
	
	public void setRebuildRequired(boolean rebuildRequired);
}
