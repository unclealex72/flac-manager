package uk.co.unclealex.music.web.actions;

import java.io.IOException;

import com.opensymphony.xwork2.Preparable;

public class AlbumAction extends AbstractAlbumCoverAction implements Preparable {

	@Override
	public void prepare() {
		populateCovers();
	}
	
	@Override
	public void doExecute() throws IOException {
		// Nothing to do.
	}
}
