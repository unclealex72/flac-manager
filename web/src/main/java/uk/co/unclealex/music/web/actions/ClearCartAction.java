package uk.co.unclealex.music.web.actions;

public class ClearCartAction extends FlacAction {

	@Override
	public String execute() {
		getDownloadCartBean().getSelections().clear();
		return SUCCESS;
	}
}