package uk.co.unclealex.music.gwt.client.view;

import java.util.SortedMap;

import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;

import com.google.gwt.user.client.ui.Widget;

public interface NavigationView {

	public interface Presenter {
		public void showArtist(ArtistInformationBean artistInformationBean);
		public void hideArtist();
	}
	
  public void setPresenter(Presenter presenter);

  public void setFirstLetters(SortedMap<Character, String> firstLetters);
  
	public void initialise();

	public void showArtist(ArtistInformationBean artistInformationBean, String token);

	public void hideArtist();

	Widget asWidget();
}
