package uk.co.unclealex.music.web.actions;

import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.unclealex.flacconverter.LetterAwareAction;
import uk.co.unclealex.music.web.flac.model.FlacArtistBean;
import uk.co.unclealex.music.web.flac.model.FlacBean;
import uk.co.unclealex.music.web.flac.visitor.FlacVisitor;
import uk.co.unclealex.music.web.flac.visitor.NoOpFlacVisitor;

public class ArtistsAction extends LetterAwareAction {

	private SortedSet<FlacArtistBean> i_flacArtistBeans;

	@Override
	public String execute() {
		final SortedSet<FlacArtistBean> flacArtistBeans = 
			new TreeSet<FlacArtistBean>(getFlacArtistDao().getArtistsBeginningWith(getLetter()));
		FlacVisitor removeArtistVisitor = new NoOpFlacVisitor() {
			@Override
			public void visit(FlacArtistBean flacArtistBean) {
				flacArtistBeans.remove(flacArtistBean);
			}
		};
		for (FlacBean flacBean : getDownloadCartBean().getSelections()) {
			flacBean.accept(removeArtistVisitor);
		}
		setFlacArtistBeans(flacArtistBeans);
		return SUCCESS;
	}
	
	public SortedSet<FlacArtistBean> getFlacArtistBeans() {
		return i_flacArtistBeans;
	}
	public void setFlacArtistBeans(SortedSet<FlacArtistBean> flacArtistBeans) {
		i_flacArtistBeans = flacArtistBeans;
	}
	
}
