package uk.co.unclealex.music.gwt.client.presenter;

import uk.co.unclealex.music.gwt.client.GwtAlbumCoverServiceAsync;
import uk.co.unclealex.music.gwt.client.action.ArtistSelectedAction;
import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;
import uk.co.unclealex.music.gwt.client.view.ArtistsView;

import com.google.gwt.event.shared.HandlerManager;

public class ArtistsPresenter extends InformationPresenter<ArtistInformationBean> {

	private char i_firstLetter;
	
	public ArtistsPresenter(
			GwtAlbumCoverServiceAsync rpcService, HandlerManager eventBus, ArtistsView view, char firstLetter) {
		super(rpcService, eventBus, view);
		i_firstLetter = firstLetter;
	}

	@Override
	protected void fetchInformation(GwtAlbumCoverServiceAsync rpcService) {
		rpcService.listArtists(getFirstLetter(), new ArtistSelectedAction.Factory(), this);
	}

	public char getFirstLetter() {
		return i_firstLetter;
	}

	public void setFirstLetter(char firstLetter) {
		i_firstLetter = firstLetter;
	}
}
