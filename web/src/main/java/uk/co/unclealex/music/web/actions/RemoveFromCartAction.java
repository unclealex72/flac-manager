package uk.co.unclealex.music.web.actions;

import java.util.List;

import uk.co.unclealex.music.core.model.EncodedBean;

public abstract class RemoveFromCartAction extends EncodedAction {

	@Override
	public String execute() {
		getDownloadCartBean().getSelections().removeAll(listBeansToRemove());
		return SUCCESS;
	}
	
	public abstract List<EncodedBean> listBeansToRemove();
}
