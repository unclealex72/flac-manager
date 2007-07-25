package uk.co.unclealex.flacconverter.actions;

import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;

public class AddAlbumsToCartAction extends AddToCartAction<FlacAlbumBean> {

	private FlacAlbumBean[] i_items;

	public FlacAlbumBean[] getItems() {
		return i_items;
	}

	public void setItems(FlacAlbumBean[] items) {
		i_items = items;
	}

	@Override
	public FlacAlbumBean[] getItemsInternal() {
		return getItems();
	}

}
