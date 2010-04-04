package uk.co.unclealex.music.base.service.titleformat;

import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;

public interface TitleFormatService {

	public String createTitle(EncodedTrackBean encodedTrackBean, OwnerBean ownerBean, boolean sanitise);	
}
