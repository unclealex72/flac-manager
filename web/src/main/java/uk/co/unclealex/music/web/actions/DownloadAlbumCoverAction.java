package uk.co.unclealex.music.web.actions;

import java.io.IOException;

import javax.jcr.RepositoryException;

import uk.co.unclealex.music.core.model.AlbumCoverSize;

public class DownloadAlbumCoverAction extends AbstractAlbumCoverAction {

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

	public void setUrl(String url) {
		i_url = url;
	}

}
