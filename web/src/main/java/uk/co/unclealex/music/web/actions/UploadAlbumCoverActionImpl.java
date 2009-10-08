package uk.co.unclealex.music.web.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.jcr.RepositoryException;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.model.AlbumCoverSize;

@Transactional
public class UploadAlbumCoverActionImpl extends AbstractAlbumCoverAction implements UploadAlbumCoverAction {

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
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.web.actions.UploadAlbumCoverAction#setFile(java.io.File)
	 */
	public void setFile(File file) {
		i_file = file;
	}
	public String getFileContentType() {
		return i_fileContentType;
	}
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.web.actions.UploadAlbumCoverAction#setFileContentType(java.lang.String)
	 */
	public void setFileContentType(String fileContentType) {
		i_fileContentType = fileContentType;
	}
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.web.actions.UploadAlbumCoverAction#getFileFileName()
	 */
	public String getFileFileName() {
		return i_fileFileName;
	}
	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.web.actions.UploadAlbumCoverAction#setFileFileName(java.lang.String)
	 */
	public void setFileFileName(String fileFileName) {
		i_fileFileName = fileFileName;
	}
	
}
