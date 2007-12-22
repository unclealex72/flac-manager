package uk.co.unclealex.music.web.actions;

import uk.co.unclealex.music.core.model.EncodedArtistBean;

public class AddArtistsToCartAction extends AddToCartAction<EncodedArtistBean> {

	private EncodedArtistBean[] i_items;

	public EncodedArtistBean[] getItems() {
		return i_items;
	}

	public void setItems(EncodedArtistBean[] items) {
		i_items = items;
	}
	
	@Override
	public EncodedArtistBean[] getItemsInternal() {
		return getItems();
	}
}
