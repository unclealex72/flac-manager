package uk.co.unclealex.music.web.actions;

import uk.co.unclealex.flacconverter.AddToCartAction;
import uk.co.unclealex.music.web.flac.model.FlacTrackBean;

public class AddTracksToCartAction extends AddToCartAction<FlacTrackBean> {

	private FlacTrackBean[] i_items;

	public FlacTrackBean[] getItems() {
		return i_items;
	}

	public void setItems(FlacTrackBean[] items) {
		i_items = items;
	}

	@Override
	public FlacTrackBean[] getItemsInternal() {
		return getItems();
	}

}
