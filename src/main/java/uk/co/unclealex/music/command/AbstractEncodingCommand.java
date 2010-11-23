package uk.co.unclealex.music.command;

import uk.co.unclealex.music.encoding.EncodingService;

public abstract class AbstractEncodingCommand extends SpringCommand<EncodingService> {

	@Override
	protected Class<? extends EncodingService> getServiceClass() {
		return EncodingService.class;
	}
}
