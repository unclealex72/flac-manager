package uk.co.unclealex.music.legacy.command;

import uk.co.unclealex.executable.CommandLine;
import uk.co.unclealex.music.legacy.encoding.EncodingService;

public abstract class AbstractEncodingCommand<C extends CommandLine> extends AbstractMusicCommand<C, EncodingService> {

	@Override
	protected Class<? extends EncodingService> getServiceClass() {
		return EncodingService.class;
	}
}
