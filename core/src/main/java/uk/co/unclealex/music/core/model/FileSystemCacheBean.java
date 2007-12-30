package uk.co.unclealex.music.core.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

@Entity(name="fileSystemCacheBean")
@Table(name="file_system_cache")
public class FileSystemCacheBean implements Serializable {

	private int i_id;
	private boolean i_rebuildRequired;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return i_id;
	}

	public void setId(int id) {
		i_id = id;
	}

	@NotNull
	public boolean isRebuildRequired() {
		return i_rebuildRequired;
	}

	public void setRebuildRequired(boolean rebuildRequired) {
		i_rebuildRequired = rebuildRequired;
	}
}
