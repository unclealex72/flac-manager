package uk.co.unclealex.flacconverter.actions;

import java.util.List;

import uk.co.unclealex.flacconverter.flac.model.FlacBean;

public abstract class RemoveFromCartAction extends FlacAction {

	@Override
	public String execute() {
		getDownloadCartBean().getSelections().removeAll(listBeansToRemove());
		return SUCCESS;
	}
	
	public abstract List<FlacBean> listBeansToRemove();
}
