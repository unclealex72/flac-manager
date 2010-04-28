package uk.co.unclealex.music.gwt.client.presenter;

import java.util.SortedMap;

import uk.co.unclealex.music.gwt.client.FailureAwareAsyncCallback;
import uk.co.unclealex.music.gwt.client.GwtAlbumCoverServiceAsync;
import uk.co.unclealex.music.gwt.client.action.ArtistSelectedAction;
import uk.co.unclealex.music.gwt.client.action.LetterSelectedAction;
import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;
import uk.co.unclealex.music.gwt.client.view.NavigationView;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class NavigationPresenter implements Presenter, NavigationView.Presenter {

  private final GwtAlbumCoverServiceAsync rpcService;
  private final HandlerManager eventBus;
  private final NavigationView view;
  
  public NavigationPresenter(GwtAlbumCoverServiceAsync rpcService, HandlerManager eventBus, NavigationView view) {
    this.rpcService = rpcService;
    this.eventBus = eventBus;
    this.view = view;
    this.view.setPresenter(this);
  }

	public void go(HasWidgets container) {
    container.clear();
    container.add(view.asWidget());
    AsyncCallback<SortedMap<Character, String>> firstLetterAsyncService = new FailureAwareAsyncCallback<SortedMap<Character,String>>() {
    	public void onSuccess(SortedMap<Character, String> tokensByFirstLetter) {
    		view.setFirstLetters(tokensByFirstLetter);
    	}
		};
		rpcService.listFirstLetters(new LetterSelectedAction.Factory(), firstLetterAsyncService);
	}

	public void showArtist(final ArtistInformationBean artistInformationBean) {
		AsyncCallback<String> callback = new FailureAwareAsyncCallback<String>() {
			public void onSuccess(String token) {
				view.showArtist(artistInformationBean, token);
			}
		};
		rpcService.serialise(new ArtistSelectedAction(artistInformationBean), callback );
	}

	public void hideArtist() {
		view.hideArtist();
	}
	
	public HandlerManager getEventBus() {
		return eventBus;
	}
}
