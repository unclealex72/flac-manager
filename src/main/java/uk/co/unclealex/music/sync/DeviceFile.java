package uk.co.unclealex.music.sync;


public class DeviceFile extends RelativePathFile<DeviceFile> {

	private String i_id;
	private long i_actualLastModified;
	
	public DeviceFile(String id, String relativePath, long lastModified) {
		super(relativePath);
		i_id = id;
		i_actualLastModified = lastModified;
	}

	public String getId() {
		return i_id;
	}

	public long getActualLastModified() {
		return i_actualLastModified;
	}
}
