package uk.co.unclealex.music.command;

import uk.co.unclealex.music.encoding.RenamingService;

public abstract class AbstractRenamingCommand extends SpringCommand<RenamingService> {

	@Override
	protected Class<? extends RenamingService> getServiceClass() {
		return RenamingService.class;
	}
}
