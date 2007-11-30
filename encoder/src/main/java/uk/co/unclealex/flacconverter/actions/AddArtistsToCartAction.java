package uk.co.unclealex.flacconverter.actions;

import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;

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
