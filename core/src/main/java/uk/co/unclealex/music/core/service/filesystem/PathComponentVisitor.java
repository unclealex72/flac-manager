package uk.co.unclealex.music.core.service.filesystem;

public interface PathComponentVisitor {

	public void visit(VisiblePathComponent visiblePathComponent);
	
	public void visit(PathComponent pathComponent);
}
