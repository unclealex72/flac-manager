package uk.co.unclealex.music.gwt.client.presenter;

import uk.co.unclealex.music.gwt.client.GwtAlbumCoverServiceAsync;
import uk.co.unclealex.music.gwt.client.action.MissingCoversSelectedAction;
import uk.co.unclealex.music.gwt.client.event.AlbumSelectedEvent;
import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;
import uk.co.unclealex.music.gwt.client.view.AlbumsView;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;

public class MissingCoversPresenter extends InformationPresenter<AlbumInformationBean> {

	public MissingCoversPresenter(
			GwtAlbumCoverServiceAsync rpcService, HandlerManager eventBus, AlbumsView view) {
		super(rpcService, eventBus, view);
	}

	@Override
	protected void fetchInformation(GwtAlbumCoverServiceAsync rpcService) {
		rpcService.listMissingCovers(new MissingCoversSelectedAction.Factory(), this);
	}
}
