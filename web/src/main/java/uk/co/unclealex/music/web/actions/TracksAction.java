package uk.co.unclealex.music.web.actions;

import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.visitor.EncodedVisitor;
import uk.co.unclealex.music.core.visitor.NoOpEncodedVisitor;

public class TracksAction extends LetterAwareAction {

	private SortedSet<EncodedTrackBean> i_encodedTrackBeans;
	private EncodedAlbumBean i_encodedAlbum;
	
	@Override
	public String execute() {
		final SortedSet<EncodedTrackBean> encodedTrackBeans = 
			new TreeSet<EncodedTrackBean>(getEncodedAlbum().getEncodedTrackBeans());
		EncodedVisitor removeArtistVisitor = new NoOpEncodedVisitor() {
			@Override
			public void visit(EncodedTrackBean encodedTrackBean) {
				encodedTrackBeans.remove(encodedTrackBean);
			}
		};
		for (EncodedBean encodedBean : getDownloadCartBean().getSelections()) {
			encodedBean.accept(removeArtistVisitor);
		}
		setEncodedTrackBeans(encodedTrackBeans);
		return SUCCESS;
	}
	
	public SortedSet<EncodedTrackBean> getEncodedTrackBeans() {
		return i_encodedTrackBeans;
	}
	public void setEncodedTrackBeans(SortedSet<EncodedTrackBean> encodedTrackBeans) {
		i_encodedTrackBeans = encodedTrackBeans;
	}

	public EncodedAlbumBean getEncodedAlbum() {
		return i_encodedAlbum;
	}

	public void setEncodedAlbum(EncodedAlbumBean encodedAlbum) {
		i_encodedAlbum = encodedAlbum;
	}
}
