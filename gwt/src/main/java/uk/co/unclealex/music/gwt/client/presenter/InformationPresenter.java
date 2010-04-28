package uk.co.unclealex.music.gwt.client.presenter;

import java.util.SortedMap;

import uk.co.unclealex.music.gwt.client.Fail;
import uk.co.unclealex.music.gwt.client.FailureAwareAsyncCallback;
import uk.co.unclealex.music.gwt.client.GwtAlbumCoverServiceAsync;
import uk.co.unclealex.music.gwt.client.model.InformationBean;
import uk.co.unclealex.music.gwt.client.view.InformationView;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;

public abstract class InformationPresenter<I extends InformationBean<I>> implements Presenter, 
  InformationView.Presenter<I>, AsyncCallback<SortedMap<I, String>> {  

  private final GwtAlbumCoverServiceAsync rpcService;
  private final HandlerManager eventBus;
  private final InformationView<I> view;
  
  public InformationPresenter(GwtAlbumCoverServiceAsync rpcService, HandlerManager eventBus, InformationView<I> view) {
    this.rpcService = rpcService;
    this.eventBus = eventBus;
    this.view = view;
    this.view.setPresenter(this);
  }

  public void setImageUrl(final Image image, I informationBean) {
  	AsyncCallback<String> callback = new FailureAwareAsyncCallback<String>() {
  		public void onSuccess(String url) {
  			image.setUrl(url);
  		}
  		public void onFailure(Throwable t) {
  			image.setUrl("");
  		}
		};
		rpcService.createArtworkLink(informationBean.getUrl(), callback);
  }
  
	public void go(final HasWidgets container) {
    container.clear();
    container.add(view.asWidget());
    fetchInformation(rpcService);
  }

	protected abstract void fetchInformation(GwtAlbumCoverServiceAsync rpcService);

	public void onSuccess(SortedMap<I, String> informationBeansAndTokens) {
		view.setInformationBeansAndTokens(informationBeansAndTokens);
	}
	
	public void onFailure(Throwable t) {
		Fail.fail(t);
	}
	
	public GwtAlbumCoverServiceAsync getRpcService() {
		return rpcService;
	}

	public HandlerManager getEventBus() {
		return eventBus;
	}

	public InformationView<I> getView() {
		return view;
	}

}
