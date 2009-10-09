package uk.co.unclealex.music.encoder.action;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;


abstract class AlbumAction extends ArtistAction {

	private String i_albumCode;
	
	public AlbumAction(String artistCode, String albumCode) {
		super(artistCode);
		i_albumCode = albumCode;
	}

	public AlbumAction(EncodedAlbumBean encodedAlbumBean) {
		super(encodedAlbumBean.getEncodedArtistBean());
		i_albumCode = encodedAlbumBean.getCode();
	}

	@Override
	public void accept(EncodingActionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <E> E accept(ValueEncodingActionVisitor<E> visitor) {
		return visitor.visit(this);
	}

	public String getAlbumCode() {
		return i_albumCode;
	}
}
