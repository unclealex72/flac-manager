package uk.co.unclealex.flacconverter.actions;

import uk.co.unclealex.flacconverter.encoded.service.WritingListenerService;

public class WritingListenersAction extends FlacAction {

	private WritingListenerService i_writingListenerService;

	public WritingListenerService getWritingListenerService() {
		return i_writingListenerService;
	}

	public void setWritingListenerService(
			WritingListenerService writingListenerService) {
		i_writingListenerService = writingListenerService;
	}
}
