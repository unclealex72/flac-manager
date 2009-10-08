package uk.co.unclealex.music.encoder.action;

import uk.co.unclealex.music.base.model.EncodedArtistBean;

public class ArtistAddedAction extends ArtistAction {

	public ArtistAddedAction(EncodedArtistBean encodedArtistBean) {
		super(encodedArtistBean);
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
