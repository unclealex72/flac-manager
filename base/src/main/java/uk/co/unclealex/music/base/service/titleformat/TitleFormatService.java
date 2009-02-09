package uk.co.unclealex.music.base.service.titleformat;

import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;

public interface TitleFormatService {

	public String getTitle(EncodedTrackBean encodedTrackBean);

	public boolean isOwnerRequired();
	
	public void setTitleFormat(String titleFormat);

	public String getTitle(EncodedTrackBean trackBean, OwnerBean ownerBean);
}
