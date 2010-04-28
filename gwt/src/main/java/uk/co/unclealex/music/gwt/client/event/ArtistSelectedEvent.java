package uk.co.unclealex.music.gwt.client.event;

import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;

import com.google.gwt.event.shared.GwtEvent;

public class ArtistSelectedEvent extends GwtEvent<ArtistSelectedEventHandler> {
  public static Type<ArtistSelectedEventHandler> TYPE = new Type<ArtistSelectedEventHandler>();
  
  private ArtistInformationBean i_artistInformationBean;
  
  public ArtistSelectedEvent(ArtistInformationBean artistInformationBean) {
		super();
		i_artistInformationBean = artistInformationBean;
	}

	@Override
  public Type<ArtistSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ArtistSelectedEventHandler handler) {
    handler.onArtistSelected(this);
  }

	public ArtistInformationBean getArtistInformationBean() {
		return i_artistInformationBean;
	}

	public void setArtistInformationBean(ArtistInformationBean artistInformationBean) {
		i_artistInformationBean = artistInformationBean;
	}
}
