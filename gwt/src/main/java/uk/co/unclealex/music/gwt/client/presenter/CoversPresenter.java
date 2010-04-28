package uk.co.unclealex.music.gwt.client.presenter;

import java.util.List;

import uk.co.unclealex.music.gwt.client.FailureAwareAsyncCallback;
import uk.co.unclealex.music.gwt.client.GwtAlbumCoverServiceAsync;
import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;
import uk.co.unclealex.music.gwt.client.model.ArtworkInformationBean;
import uk.co.unclealex.music.gwt.client.view.CoverViewImpl;
import uk.co.unclealex.music.gwt.client.view.CoversView;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class CoversPresenter implements Presenter, CoversView.Presenter {

  private final GwtAlbumCoverServiceAsync rpcService;
  private final HandlerManager eventBus;
  private final CoversView view;
  
  public CoversPresenter(GwtAlbumCoverServiceAsync rpcService, HandlerManager eventBus, CoversView view) {
    this.rpcService = rpcService;
    this.eventBus = eventBus;
    this.view = view;
    this.view.setPresenter(this);
  }

	public void go(HasWidgets container) {
    container.add(view.asWidget());
	}

	public void initialise(final AlbumInformationBean albumInformationBean) {
		AsyncCallback<List<String>> callback = new FailureAwareAsyncCallback<List<String>>() {
			public void onSuccess(List<String> urls) {
				for (String url : urls) {
					CoverPresenter coverPresenter = new CoverPresenter(rpcService, eventBus, new CoverViewImpl());
					ArtworkInformationBean artworkInformationBean = new ArtworkInformationBean(albumInformationBean, url);
					coverPresenter.initialise(artworkInformationBean);
					coverPresenter.go(view.asContainer());
				}
			}
		};
		rpcService.searchForArtwork(
				albumInformationBean.getArtistInformationBean().getArtist(), 
				albumInformationBean.getAlbum(), callback);
	}
	
	public HandlerManager getEventBus() {
		return eventBus;
	}
}
