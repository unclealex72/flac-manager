package uk.co.unclealex.music.web.actions;

import uk.co.unclealex.flacconverter.AddToCartAction;
import uk.co.unclealex.music.web.flac.model.FlacArtistBean;

public class AddArtistsToCartAction extends AddToCartAction<FlacArtistBean> {

	private FlacArtistBean[] i_items;

	public FlacArtistBean[] getItems() {
		return i_items;
	}

	public void setItems(FlacArtistBean[] items) {
		i_items = items;
	}
	
	@Override
	public FlacArtistBean[] getItemsInternal() {
		return getItems();
	}
}
