package uk.co.unclealex.music.web.actions;

import java.util.LinkedList;
import java.util.List;

import uk.co.unclealex.music.core.model.EncodedBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;

public class RemoveTrackFromCartAction extends RemoveFromCartAction {

	private EncodedTrackBean i_encodedTrack;

	@Override
	public List<EncodedBean> listBeansToRemove() {
		List<EncodedBean> encodedBeans = new LinkedList<EncodedBean>();
		encodedBeans.add(getEncodedTrack());
		return encodedBeans;
	}
	
	public EncodedTrackBean getEncodedTrack() {
		return i_encodedTrack;
	}

	public void setEncodedTrack(EncodedTrackBean encodedTrack) {
		i_encodedTrack = encodedTrack;
	}
}
