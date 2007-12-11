package uk.co.unclealex.music.web.actions;

import java.util.List;

import uk.co.unclealex.flacconverter.LetterAwareAction;
import uk.co.unclealex.music.web.flac.model.FlacBean;

public abstract class AddToCartAction<E extends FlacBean> extends LetterAwareAction {

	@Override
	public String execute() {
		List<FlacBean> selections = getDownloadCartBean().getSelections();
		for (FlacBean flacBean : getItemsInternal()) {
			selections.add(flacBean);
		}
		return SUCCESS;
	}

	public abstract E[] getItemsInternal();
}
