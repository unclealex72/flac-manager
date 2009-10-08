package uk.co.unclealex.music.base.service.filesystem;

public class WrongFileTypeException extends Exception {

	private boolean i_directoryExpected;
	private String i_path;
	
	public WrongFileTypeException(String path, boolean directoryExpected) {
		super(
			String.format(
				"A %s was found when a %s was expected for path %s",
				directoryExpected?"file":"directory",
				directoryExpected?"directory":"file",
				path));
		i_path = path;
		i_directoryExpected = directoryExpected;
	}

	public boolean isDirectoryExpected() {
		return i_directoryExpected;
	}

	public String getPath() {
		return i_path;
	}
}
