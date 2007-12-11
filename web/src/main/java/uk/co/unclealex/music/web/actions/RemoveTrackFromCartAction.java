package uk.co.unclealex.music.web.actions;

import java.util.LinkedList;
import java.util.List;

import uk.co.unclealex.flacconverter.RemoveFromCartAction;
import uk.co.unclealex.music.web.flac.model.FlacBean;
import uk.co.unclealex.music.web.flac.model.FlacTrackBean;

public class RemoveTrackFromCartAction extends RemoveFromCartAction {

	private FlacTrackBean i_flacTrack;

	@Override
	public List<FlacBean> listBeansToRemove() {
		List<FlacBean> flacBeans = new LinkedList<FlacBean>();
		flacBeans.add(getFlacTrack());
		return flacBeans;
	}
	
	public FlacTrackBean getFlacTrack() {
		return i_flacTrack;
	}

	public void setFlacTrack(FlacTrackBean flacTrack) {
		i_flacTrack = flacTrack;
	}
}
