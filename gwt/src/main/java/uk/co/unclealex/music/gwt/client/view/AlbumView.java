package uk.co.unclealex.music.gwt.client.view;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public interface AlbumView {

	public interface Presenter {
		public void onSearchClicked();
	}
	
	public void setPresenter(Presenter presenter);
	
	public HasValue<String> getArtist();
	
	public HasValue<String> getAlbum();
	
	public HasValue<String> getUrl();
	
	Widget asWidget();

	public void updateAlbumArtwork(String url);
}
