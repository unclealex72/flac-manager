package uk.co.unclealex.music.web.actions;

import java.util.List;

import uk.co.unclealex.flacconverter.FlacAction;
import uk.co.unclealex.music.web.flac.model.FlacBean;

public abstract class RemoveFromCartAction extends FlacAction {

	@Override
	public String execute() {
		getDownloadCartBean().getSelections().removeAll(listBeansToRemove());
		return SUCCESS;
	}
	
	public abstract List<FlacBean> listBeansToRemove();
}
