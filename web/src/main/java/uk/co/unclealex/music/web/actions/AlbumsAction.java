package uk.co.unclealex.music.web.actions;

import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;
import uk.co.unclealex.music.core.model.EncodedBean;
import uk.co.unclealex.music.core.visitor.EncodedVisitor;
import uk.co.unclealex.music.core.visitor.NoOpEncodedVisitor;

public class AlbumsAction extends LetterAwareAction {

	private SortedSet<EncodedAlbumBean> i_encodedAlbumBeans;
	private EncodedArtistBean i_encodedArtist;
	
	@Override
	public String execute() {
		final SortedSet<EncodedAlbumBean> encodedAlbumBeans = 
			new TreeSet<EncodedAlbumBean>(getEncodedArtist().getEncodedAlbumBeans());
		EncodedVisitor removeArtistVisitor = new NoOpEncodedVisitor() {
			@Override
			public void visit(EncodedAlbumBean encodedAlbumBean) {
				encodedAlbumBeans.remove(encodedAlbumBean);
			}
		};
		for (EncodedBean encodedBean : getDownloadCartBean().getSelections()) {
			encodedBean.accept(removeArtistVisitor);
		}
		setEncodedAlbumBeans(encodedAlbumBeans);
		return SUCCESS;
	}
	
	public SortedSet<EncodedAlbumBean> getEncodedAlbumBeans() {
		return i_encodedAlbumBeans;
	}
	public void setEncodedAlbumBeans(SortedSet<EncodedAlbumBean> encodedAlbumBeans) {
		i_encodedAlbumBeans = encodedAlbumBeans;
	}

	public EncodedArtistBean getEncodedArtist() {
		return i_encodedArtist;
	}

	public void setEncodedArtist(EncodedArtistBean encodedArtist) {
		i_encodedArtist = encodedArtist;
	}
	
	
}
