package uk.co.unclealex.music.legacy.command;

import uk.co.unclealex.executable.CommandLine;
import uk.co.unclealex.music.legacy.encoding.RenamingService;

public abstract class AbstractRenamingCommand<C extends CommandLine> extends AbstractMusicCommand<C, RenamingService> {

	@Override
	protected Class<? extends RenamingService> getServiceClass() {
		return RenamingService.class;
	}
}
