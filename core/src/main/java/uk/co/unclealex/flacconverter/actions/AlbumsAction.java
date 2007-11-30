package uk.co.unclealex.flacconverter.actions;

import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;
import uk.co.unclealex.flacconverter.flac.model.FlacBean;
import uk.co.unclealex.flacconverter.flac.visitor.FlacVisitor;
import uk.co.unclealex.flacconverter.flac.visitor.NoOpFlacVisitor;

public class AlbumsAction extends LetterAwareAction {

	private SortedSet<FlacAlbumBean> i_flacAlbumBeans;
	private FlacArtistBean i_flacArtist;
	
	@Override
	public String execute() {
		final SortedSet<FlacAlbumBean> flacAlbumBeans = 
			new TreeSet<FlacAlbumBean>(getFlacArtist().getFlacAlbumBeans());
		FlacVisitor removeArtistVisitor = new NoOpFlacVisitor() {
			@Override
			public void visit(FlacAlbumBean flacAlbumBean) {
				flacAlbumBeans.remove(flacAlbumBean);
			}
		};
		for (FlacBean flacBean : getDownloadCartBean().getSelections()) {
			flacBean.accept(removeArtistVisitor);
		}
		setFlacAlbumBeans(flacAlbumBeans);
		return SUCCESS;
	}
	
	public SortedSet<FlacAlbumBean> getFlacAlbumBeans() {
		return i_flacAlbumBeans;
	}
	public void setFlacAlbumBeans(SortedSet<FlacAlbumBean> flacAlbumBeans) {
		i_flacAlbumBeans = flacAlbumBeans;
	}

	public FlacArtistBean getFlacArtist() {
		return i_flacArtist;
	}

	public void setFlacArtist(FlacArtistBean flacArtist) {
		i_flacArtist = flacArtist;
	}
	
	
}
