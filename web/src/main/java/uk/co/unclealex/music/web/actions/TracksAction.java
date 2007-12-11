package uk.co.unclealex.music.web.actions;

import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.unclealex.flacconverter.LetterAwareAction;
import uk.co.unclealex.music.web.flac.model.FlacAlbumBean;
import uk.co.unclealex.music.web.flac.model.FlacBean;
import uk.co.unclealex.music.web.flac.model.FlacTrackBean;
import uk.co.unclealex.music.web.flac.visitor.FlacVisitor;
import uk.co.unclealex.music.web.flac.visitor.NoOpFlacVisitor;

public class TracksAction extends LetterAwareAction {

	private SortedSet<FlacTrackBean> i_flacTrackBeans;
	private FlacAlbumBean i_flacAlbum;
	
	@Override
	public String execute() {
		final SortedSet<FlacTrackBean> flacTrackBeans = 
			new TreeSet<FlacTrackBean>(getFlacAlbum().getFlacTrackBeans());
		FlacVisitor removeArtistVisitor = new NoOpFlacVisitor() {
			@Override
			public void visit(FlacTrackBean flacTrackBean) {
				flacTrackBeans.remove(flacTrackBean);
			}
		};
		for (FlacBean flacBean : getDownloadCartBean().getSelections()) {
			flacBean.accept(removeArtistVisitor);
		}
		setFlacTrackBeans(flacTrackBeans);
		return SUCCESS;
	}
	
	public SortedSet<FlacTrackBean> getFlacTrackBeans() {
		return i_flacTrackBeans;
	}
	public void setFlacTrackBeans(SortedSet<FlacTrackBean> flacTrackBeans) {
		i_flacTrackBeans = flacTrackBeans;
	}

	public FlacAlbumBean getFlacAlbum() {
		return i_flacAlbum;
	}

	public void setFlacAlbum(FlacAlbumBean flacAlbum) {
		i_flacAlbum = flacAlbum;
	}
}
