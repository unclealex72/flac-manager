package uk.co.unclealex.music.commands;

import java.io.IOException;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;

@uk.co.unclealex.spring.Main
public class Covers extends Main {

	private AlbumCoverService i_albumCoverService;
	
	@Override
	public void execute() throws IOException {
		getAlbumCoverService().downloadMissing();
	}
	
	public static void main(String[] args) throws Exception {
		Main.execute(new Covers());
	}

	public AlbumCoverService getAlbumCoverService() {
		return i_albumCoverService;
	}

	public void setAlbumCoverService(AlbumCoverService albumCoverService) {
		i_albumCoverService = albumCoverService;
	}
}
