package uk.co.unclealex.music.gwt.client.presenter;

import uk.co.unclealex.music.gwt.client.GwtAlbumCoverServiceAsync;
import uk.co.unclealex.music.gwt.client.action.AlbumSelectedAction;
import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;
import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;
import uk.co.unclealex.music.gwt.client.view.AlbumsView;

import com.google.gwt.event.shared.HandlerManager;

public class AlbumsPresenter extends InformationPresenter<AlbumInformationBean> {

	private ArtistInformationBean i_artistInformationBean;
	
	public AlbumsPresenter(
			GwtAlbumCoverServiceAsync rpcService, HandlerManager eventBus, AlbumsView view, ArtistInformationBean artistInformationBean) {
		super(rpcService, eventBus, view);
		i_artistInformationBean = artistInformationBean;
	}

	@Override
	protected void fetchInformation(GwtAlbumCoverServiceAsync rpcService) {
		rpcService.listAlbums(getArtistInformationBean(), new AlbumSelectedAction.Factory(), this);
	}

	public ArtistInformationBean getArtistInformationBean() {
		return i_artistInformationBean;
	}

	public void setArtistInformationBean(ArtistInformationBean artistInformationBean) {
		i_artistInformationBean = artistInformationBean;
	}
}
