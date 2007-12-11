package uk.co.unclealex.music.web.actions;

import uk.co.unclealex.flacconverter.AddToCartAction;
import uk.co.unclealex.music.web.flac.model.FlacAlbumBean;

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
