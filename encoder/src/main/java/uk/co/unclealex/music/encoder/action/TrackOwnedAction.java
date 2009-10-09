package uk.co.unclealex.music.encoder.action;

import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;

public class TrackOwnedAction extends TrackAction {

	private String i_ownerName;
	
	public TrackOwnedAction(String ownerName, String artistCode, String albumCode, int trackNumber, String trackCode,
			String extension) {
		super(artistCode, albumCode, trackNumber, trackCode, extension);
		i_ownerName = ownerName;
	}

	public TrackOwnedAction(String ownerName, EncodedTrackBean encodedTrackBean) {
		super(encodedTrackBean);
		i_ownerName = ownerName;
	}

	public TrackOwnedAction(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean) {
		this(ownerBean.getName(), encodedTrackBean);
		i_ownerName = ownerBean.getName();
	}

	@Override
	public void accept(EncodingActionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <E> E accept(ValueEncodingActionVisitor<E> visitor) {
		return visitor.visit(this);
	}

	public String getOwnerName() {
		return i_ownerName;
	}	
}
