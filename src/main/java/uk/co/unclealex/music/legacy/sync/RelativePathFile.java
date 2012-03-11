package uk.co.unclealex.music.legacy.sync;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


public abstract class RelativePathFile<R extends RelativePathFile<R>> implements Comparable<R> {

	private static final DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTime();
	
	private String i_relativePath;

	public RelativePathFile(String relativePath) {
		super();
		i_relativePath = relativePath;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		return 
			obj != null && 
			obj.getClass().equals(getClass()) && 
			getRelativePath().equals(((RelativePathFile) obj).getRelativePath());
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", getRelativePath(), FORMATTER.print(getLastModified()));
	}
	
	@Override
	public int hashCode() {
		return getRelativePath().hashCode();
	}

	public int compareTo(R o) {
		return getRelativePath().compareTo(o.getRelativePath());
	};
	
	protected abstract long getActualLastModified();

	public String getRelativePath() {
		return i_relativePath;
	}
	
	public long getLastModified() {
		return getActualLastModified() / 1000l * 1000l;
	}
}
