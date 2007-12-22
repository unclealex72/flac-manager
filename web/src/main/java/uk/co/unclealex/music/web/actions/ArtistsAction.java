package uk.co.unclealex.music.web.actions;

import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedBean;
import uk.co.unclealex.music.core.visitor.EncodedVisitor;
import uk.co.unclealex.music.core.visitor.NoOpEncodedVisitor;

public class ArtistsAction extends LetterAwareAction {

	private SortedSet<EncodedArtistBean> i_encodedArtistBeans;

	@Override
	public String execute() {
		final SortedSet<EncodedArtistBean> encodedArtistBeans = 
			new TreeSet<EncodedArtistBean>(getEncodedArtistDao().findByFirstLetter(getLetter()));
		EncodedVisitor removeArtistVisitor = new NoOpEncodedVisitor() {
			@Override
			public void visit(EncodedArtistBean encodedArtistBean) {
				encodedArtistBeans.remove(encodedArtistBean);
			}
		};
		for (EncodedBean encodedBean : getDownloadCartBean().getSelections()) {
			encodedBean.accept(removeArtistVisitor);
		}
		setEncodedArtistBeans(encodedArtistBeans);
		return SUCCESS;
	}
	
	public SortedSet<EncodedArtistBean> getEncodedArtistBeans() {
		return i_encodedArtistBeans;
	}
	public void setEncodedArtistBeans(SortedSet<EncodedArtistBean> encodedArtistBeans) {
		i_encodedArtistBeans = encodedArtistBeans;
	}
	
}
