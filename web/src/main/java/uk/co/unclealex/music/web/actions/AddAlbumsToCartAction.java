package uk.co.unclealex.music.web.actions;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;


public class AddAlbumsToCartAction extends AddToCartAction<EncodedAlbumBean> {

	private EncodedAlbumBean[] i_items;

	public EncodedAlbumBean[] getItems() {
		return i_items;
	}

	public void setItems(EncodedAlbumBean[] items) {
		i_items = items;
	}

	@Override
	public EncodedAlbumBean[] getItemsInternal() {
		return getItems();
	}

}
