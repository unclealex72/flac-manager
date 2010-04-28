package uk.co.unclealex.music.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class MissingCoversSelectedEvent extends GwtEvent<MissingCoversSelectedEventHandler> {
  public static Type<MissingCoversSelectedEventHandler> TYPE = new Type<MissingCoversSelectedEventHandler>();
  
	@Override
  public Type<MissingCoversSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(MissingCoversSelectedEventHandler handler) {
    handler.onMissingCoversSelected(this);
  }
}
