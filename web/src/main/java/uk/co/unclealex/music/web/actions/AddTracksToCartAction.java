package uk.co.unclealex.music.web.actions;

import uk.co.unclealex.music.core.model.EncodedTrackBean;

public class AddTracksToCartAction extends AddToCartAction<EncodedTrackBean> {

	private EncodedTrackBean[] i_items;

	public EncodedTrackBean[] getItems() {
		return i_items;
	}

	public void setItems(EncodedTrackBean[] items) {
		i_items = items;
	}

	@Override
	public EncodedTrackBean[] getItemsInternal() {
		return getItems();
	}

}
