package uk.co.unclealex.music.encoder.action;

import uk.co.unclealex.music.base.model.EncodedArtistBean;

abstract class ArtistAction extends EncodingAction {

	private String i_artistCode;
	
	public ArtistAction(EncodedArtistBean encodedArtistBean) {
		super();
		i_artistCode = encodedArtistBean.getCode();
	}

	@Override
	public void accept(EncodingActionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <E> E accept(ValueEncodingActionVisitor<E> visitor) {
		return visitor.visit(this);
	}

	public String getArtistCode() {
		return i_artistCode;
	}
}
