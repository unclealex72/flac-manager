package uk.co.unclealex.music.core.service.filesystem;


public interface VisiblePathComponent extends PathComponent {

	public void setPathComponent(String pathComponent) throws PathNotFoundException;
}
