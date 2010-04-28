package uk.co.unclealex.music.gwt.client.presenter;

import uk.co.unclealex.music.gwt.client.FailureAwareAsyncCallback;
import uk.co.unclealex.music.gwt.client.GwtAlbumCoverServiceAsync;
import uk.co.unclealex.music.gwt.client.action.ArtworkSelectedAction;
import uk.co.unclealex.music.gwt.client.model.ArtworkInformationBean;
import uk.co.unclealex.music.gwt.client.view.CoverView;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class CoverPresenter implements Presenter, CoverView.Presenter {

  private final GwtAlbumCoverServiceAsync rpcService;
  private final HandlerManager eventBus;
  private final CoverView view;
  private ArtworkInformationBean artworkInformationBean;
  
  public CoverPresenter(GwtAlbumCoverServiceAsync rpcService, HandlerManager eventBus, CoverView view) {
    this.rpcService = rpcService;
    this.eventBus = eventBus;
    this.view = view;
    this.view.setPresenter(this);
  }

	public void go(HasWidgets container) {
    container.add(view.asWidget());
	}

	public void initialise(ArtworkInformationBean artworkInformationBean) {
		this.artworkInformationBean = artworkInformationBean;
    AsyncCallback<String> callback = new FailureAwareAsyncCallback<String>() {
    	public void onSuccess(String actionToken) {
    		view.initialise(CoverPresenter.this.artworkInformationBean, actionToken);
    	}
		};
		rpcService.serialise(new ArtworkSelectedAction(artworkInformationBean), callback);
	}
	
	public HandlerManager getEventBus() {
		return eventBus;
	}
}
