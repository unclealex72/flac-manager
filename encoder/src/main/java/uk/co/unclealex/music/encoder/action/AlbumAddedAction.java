package uk.co.unclealex.music.encoder.action;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;

public class AlbumAddedAction extends AlbumAction {

	public AlbumAddedAction(String artistCode, String albumCode) {
		super(artistCode, albumCode);
	}

	public AlbumAddedAction(EncodedAlbumBean encodedAlbumBean) {
		super(encodedAlbumBean);
	}

	@Override
	public void accept(EncodingActionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <E> E accept(ValueEncodingActionVisitor<E> visitor) {
		return visitor.visit(this);
	}
}
