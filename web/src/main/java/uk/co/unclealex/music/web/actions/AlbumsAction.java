package uk.co.unclealex.music.web.actions;

import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.unclealex.flacconverter.LetterAwareAction;
import uk.co.unclealex.music.web.flac.model.FlacAlbumBean;
import uk.co.unclealex.music.web.flac.model.FlacArtistBean;
import uk.co.unclealex.music.web.flac.model.FlacBean;
import uk.co.unclealex.music.web.flac.visitor.FlacVisitor;
import uk.co.unclealex.music.web.flac.visitor.NoOpFlacVisitor;

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
