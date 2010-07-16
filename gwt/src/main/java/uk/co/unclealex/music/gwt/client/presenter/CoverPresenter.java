package uk.co.unclealex.music.gwt.client.presenter;

import uk.co.unclealex.music.gwt.client.GwtAlbumCoverServiceAsync;
import uk.co.unclealex.music.gwt.client.event.ArtworkSelectedEvent;
import uk.co.unclealex.music.gwt.client.model.ArtworkInformationBean;
import uk.co.unclealex.music.gwt.client.view.CoverView;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

public class CoverPresenter implements Presenter, CoverView.Presenter {

  private final HandlerManager eventBus;
  private final CoverView view;
  private ArtworkInformationBean artworkInformationBean;
  
  public CoverPresenter(GwtAlbumCoverServiceAsync rpcService, HandlerManager eventBus, CoverView view) {
    this.eventBus = eventBus;
    this.view = view;
    this.view.setPresenter(this);
  }

	public void go(HasWidgets container) {
    container.add(view.asWidget());
	}

	public void initialise(ArtworkInformationBean artworkInformationBean) {
		this.artworkInformationBean = artworkInformationBean;
		view.initialise(artworkInformationBean);
	}
	
	@Override
	public void onClicked() {
		eventBus.fireEvent(new ArtworkSelectedEvent(artworkInformationBean));
		
	}
	
	public HandlerManager getEventBus() {
		return eventBus;
	}
}
