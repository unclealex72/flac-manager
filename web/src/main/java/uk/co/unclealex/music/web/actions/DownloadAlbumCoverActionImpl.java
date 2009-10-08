package uk.co.unclealex.music.web.actions;

import java.io.IOException;

import javax.jcr.RepositoryException;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.model.AlbumCoverSize;

@Transactional
public class DownloadAlbumCoverActionImpl extends AbstractAlbumCoverAction implements DownloadAlbumCoverAction {

	private String i_url;
	
	@Override
	public void doExecute() throws IOException, RepositoryException {
		getAlbumCoverService().saveAndSelectCover(
				getFlacAlbumBean(), getUrl(), null, AlbumCoverSize.LARGE);
	}

	@Override
	public void fail(Exception e) {
		addFieldError("url", "Could not download url " + getUrl() + ": " + e.getMessage());
	}
	
	public String getUrl() {
		return i_url;
	}

	/* (non-Javadoc)
	 * @see uk.co.unclealex.music.web.actions.DownloadAlbumCoverAction#setUrl(java.lang.String)
	 */
	public void setUrl(String url) {
		i_url = url;
	}

}
