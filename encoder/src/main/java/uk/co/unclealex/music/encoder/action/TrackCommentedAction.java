package uk.co.unclealex.music.encoder.action;

import uk.co.unclealex.music.base.model.EncodedTrackBean;

public class TrackCommentedAction extends TrackAction {

	private String i_url;
	
	public TrackCommentedAction(String artistCode, String albumCode, int trackNumber, String trackCode, String extension, String url) {
		super(artistCode, albumCode, trackNumber, trackCode, extension);
		i_url = url;
	}

	public TrackCommentedAction(EncodedTrackBean encodedTrackBean, String url) {
		super(encodedTrackBean);
		i_url = url;
	}

	@Override
	public void accept(EncodingActionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <E> E accept(ValueEncodingActionVisitor<E> visitor) {
		return visitor.visit(this);
	}

	public String getUrl() {
		return i_url;
	}

}
