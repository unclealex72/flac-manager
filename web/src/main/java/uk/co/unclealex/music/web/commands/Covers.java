package uk.co.unclealex.music.web.commands;

import java.io.IOException;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;
import uk.co.unclealex.music.commands.Command;

@Transactional(rollbackFor=Exception.class)
public class Covers implements Command {

	private AlbumCoverService i_albumCoverService;
	
	@Override
	public void execute(String[] args) throws IOException {
		getAlbumCoverService().downloadMissing();
	}
	
	public AlbumCoverService getAlbumCoverService() {
		return i_albumCoverService;
	}

	public void setAlbumCoverService(AlbumCoverService albumCoverService) {
		i_albumCoverService = albumCoverService;
	}
}
