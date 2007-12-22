package uk.co.unclealex.music.web.actions;

import java.util.List;

import uk.co.unclealex.music.core.model.EncodedBean;

public abstract class AddToCartAction<E extends EncodedBean> extends LetterAwareAction {

	@Override
	public String execute() {
		List<EncodedBean> selections = getDownloadCartBean().getSelections();
		for (EncodedBean flacBean : getItemsInternal()) {
			selections.add(flacBean);
		}
		return SUCCESS;
	}

	public abstract E[] getItemsInternal();
}
