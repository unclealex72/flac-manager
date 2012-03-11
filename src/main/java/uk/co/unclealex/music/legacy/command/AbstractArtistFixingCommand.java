package uk.co.unclealex.music.legacy.command;

import java.io.File;

import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.unclealex.executable.CommandLine;
import uk.co.unclealex.music.legacy.ArtistFixingService;

public abstract class AbstractArtistFixingCommand<C extends AbstractArtistFixingCommandLine> extends AbstractMusicCommand<C, ArtistFixingService> {

	@Override
	protected Class<? extends ArtistFixingService> getServiceClass() {
		return ArtistFixingService.class;
	}
}

interface AbstractArtistFixingCommandLine extends CommandLine {
	
	@Option(description="The cache file for artists and their tracks.")
	public File getCache();
}