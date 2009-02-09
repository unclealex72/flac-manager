package uk.co.unclealex.music.web.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.jcr.RepositoryException;

import uk.co.unclealex.music.base.model.AlbumCoverSize;

public class UploadAlbumCoverAction extends AbstractAlbumCoverAction {

	private File i_file;
	private String i_fileContentType;
	private String i_fileFileName;
	
	@Override
	public void doExecute() throws RepositoryException, IOException {
		getAlbumCoverService().saveAndSelectCover(
				getFlacAlbumBean(), "file://" + getFileFileName(), new FileInputStream(getFile()), AlbumCoverSize.LARGE);
	}
	
	@Override
	public void fail(Exception e) {
		addFieldError("file", "Could not upload file " + getFileFileName() + ": " + e.getMessage());
	}
	
	public File getFile() {
		return i_file;
	}
	public void setFile(File file) {
		i_file = file;
	}
	public String getFileContentType() {
		return i_fileContentType;
	}
	public void setFileContentType(String fileContentType) {
		i_fileContentType = fileContentType;
	}
	public String getFileFileName() {
		return i_fileFileName;
	}
	public void setFileFileName(String fileFileName) {
		i_fileFileName = fileFileName;
	}
	
}
