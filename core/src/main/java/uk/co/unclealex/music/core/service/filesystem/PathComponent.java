package uk.co.unclealex.music.core.service.filesystem;

import java.util.SortedSet;

public interface PathComponent {
	
	public void setContext(Context context);
	/**
	 * Return the names of the available children, or null if this path is not a directory.
	 * @return
	 */
	public SortedSet<String> getChildren();

	public void accept(PathComponentVisitor pathComponentVisitor);
}
