package uk.co.unclealex.music.gwt.client.presenter;

import uk.co.unclealex.music.gwt.client.FailureAwareAsyncCallback;
import uk.co.unclealex.music.gwt.client.GwtAlbumCoverServiceAsync;
import uk.co.unclealex.music.gwt.client.event.SearchArtworkEvent;
import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;
import uk.co.unclealex.music.gwt.client.model.SearchInformationBean;
import uk.co.unclealex.music.gwt.client.view.AlbumView;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class AlbumPresenter implements Presenter, AlbumView.Presenter {

  private final GwtAlbumCoverServiceAsync rpcService;
  private final HandlerManager eventBus;
  private final AlbumView view;
  private AlbumInformationBean albumInformationBean;
  
  public AlbumPresenter(GwtAlbumCoverServiceAsync rpcService, HandlerManager eventBus, AlbumView view, AlbumInformationBean albumInformationBean) {
    this.rpcService = rpcService;
    this.eventBus = eventBus;
    this.view = view;
    this.albumInformationBean = albumInformationBean;
    this.view.setPresenter(this);
  }

	public void go(HasWidgets container) {
    container.clear();
    container.add(view.asWidget());
    view.getArtist().setValue(albumInformationBean.getArtistInformationBean().getArtist());
    view.getAlbum().setValue(albumInformationBean.getAlbum());
    AsyncCallback<String> callback = new FailureAwareAsyncCallback<String>() {
    	public void onSuccess(String url) {
    		view.updateAlbumArtwork(url);
    	}
		};
		rpcService.createArtworkLink(albumInformationBean.getUrl(), callback);
	}

	public void onSearchClicked() {
		eventBus.fireEvent(
			new SearchArtworkEvent(
				new SearchInformationBean(albumInformationBean, view.getArtist().getValue(), view.getAlbum().getValue())));
	}
	
	public HandlerManager getEventBus() {
		return eventBus;
	}
}
