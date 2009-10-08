package uk.co.unclealex.music.encoder.action;

import uk.co.unclealex.music.base.model.EncodedTrackBean;

abstract class TrackAction extends AlbumAction {

	private int i_trackNumber;
	private String i_trackCode;
	private String i_extension;
	
	protected TrackAction(EncodedTrackBean encodedTrackBean) {
		super(encodedTrackBean.getEncodedAlbumBean());
		i_trackNumber = encodedTrackBean.getTrackNumber();
		i_trackCode = encodedTrackBean.getCode();
		i_extension = encodedTrackBean.getEncoderBean().getExtension();
	}

	@Override
	public void accept(EncodingActionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <E> E accept(ValueEncodingActionVisitor<E> visitor) {
		return visitor.visit(this);
	}

	public int getTrackNumber() {
		return i_trackNumber;
	}
	
	public String getTrackCode() {
		return i_trackCode;
	}
	
	public String getExtension() {
		return i_extension;
	}
}
