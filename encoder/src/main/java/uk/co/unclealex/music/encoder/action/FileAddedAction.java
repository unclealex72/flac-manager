package uk.co.unclealex.music.encoder.action;

import uk.co.unclealex.music.base.model.EncodedTrackBean;

public class FileAddedAction extends TrackAction {

	private String i_path;
	
	public FileAddedAction(String path, EncodedTrackBean encodedTrackBean) {
		super(encodedTrackBean);
		i_path = path;
	}

	@Override
	public void accept(EncodingActionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <E> E accept(ValueEncodingActionVisitor<E> visitor) {
		return visitor.visit(this);
	}

	public String getPath() {
		return i_path;
	}	
}
