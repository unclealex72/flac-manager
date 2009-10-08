package uk.co.unclealex.music.web.actions;

import java.io.IOException;

import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.Preparable;

@Transactional
public class AlbumActionImpl extends AbstractAlbumCoverAction implements Preparable {

	@Override
	public void prepare() {
		populateCovers();
	}
	
	@Override
	public void doExecute() throws IOException {
		// Nothing to do.
	}
}
