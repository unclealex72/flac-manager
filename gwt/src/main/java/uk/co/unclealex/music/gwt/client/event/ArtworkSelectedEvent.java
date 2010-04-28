package uk.co.unclealex.music.gwt.client.event;

import uk.co.unclealex.music.gwt.client.model.ArtworkInformationBean;

import com.google.gwt.event.shared.GwtEvent;

public class ArtworkSelectedEvent extends GwtEvent<ArtworkSelectedEventHandler> {
  public static Type<ArtworkSelectedEventHandler> TYPE = new Type<ArtworkSelectedEventHandler>();
  
  private ArtworkInformationBean i_artworkInformationBean;

	public ArtworkSelectedEvent(ArtworkInformationBean artworkInformationBean) {
		super();
		i_artworkInformationBean = artworkInformationBean;
	}

	@Override
  public Type<ArtworkSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ArtworkSelectedEventHandler handler) {
    handler.onArtworkSelected(this);
  }

	public ArtworkInformationBean getArtworkInformationBean() {
		return i_artworkInformationBean;
	}
}
