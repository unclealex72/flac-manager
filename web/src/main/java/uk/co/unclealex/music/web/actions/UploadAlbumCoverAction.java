package uk.co.unclealex.music.web.actions;

import java.io.File;

public interface UploadAlbumCoverAction {

	public void setFile(File file);

	public void setFileContentType(String fileContentType);

	public void setFileFileName(String fileFileName);

}