package uk.co.unclealex.music.gwt.client.event;

import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;

import com.google.gwt.event.shared.GwtEvent;

public class AlbumSelectedEvent extends GwtEvent<AlbumSelectedEventHandler> {
  public static Type<AlbumSelectedEventHandler> TYPE = new Type<AlbumSelectedEventHandler>();
  
  private AlbumInformationBean i_albumInformationBean;
  
  public AlbumSelectedEvent(AlbumInformationBean albumInformationBean) {
		super();
		i_albumInformationBean = albumInformationBean;
	}

	@Override
  public Type<AlbumSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AlbumSelectedEventHandler handler) {
    handler.onAlbumSelected(this);
  }

	public AlbumInformationBean getAlbumInformationBean() {
		return i_albumInformationBean;
	}

	public void setAlbumInformationBean(AlbumInformationBean albumInformationBean) {
		i_albumInformationBean = albumInformationBean;
	}
}
