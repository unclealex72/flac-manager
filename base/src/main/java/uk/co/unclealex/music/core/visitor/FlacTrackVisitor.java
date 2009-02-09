package uk.co.unclealex.music.core.visitor;

import uk.co.unclealex.music.core.model.FlacAlbumBean;
import uk.co.unclealex.music.core.model.FlacArtistBean;
import uk.co.unclealex.music.core.model.FlacTrackBean;

public abstract class FlacTrackVisitor extends FlacVisitor {

	@Override
	public void visit(FlacAlbumBean flacAlbumBean) {
		for (FlacTrackBean flacTrackBean : refresh(flacAlbumBean).getFlacTrackBeans()) {
			flacTrackBean.accept(this);
		}
	}

	@Override
	public void visit(FlacArtistBean flacArtistBean) {
		for (FlacAlbumBean flacAlbumBean : refresh(flacArtistBean).getFlacAlbumBeans()) {
			flacAlbumBean.accept(this);
		}
	}

	public FlacTrackBean refresh(FlacTrackBean flacTrackBean) {
		return flacTrackBean;
	}
	
	public FlacAlbumBean refresh(FlacAlbumBean flacAlbumBean) {
		return flacAlbumBean;
	}
	
	public FlacArtistBean refresh(FlacArtistBean flacArtistBean) {
		return flacArtistBean;
	}
}
