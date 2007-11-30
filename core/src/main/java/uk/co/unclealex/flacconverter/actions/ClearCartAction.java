package uk.co.unclealex.flacconverter.actions;

public class ClearCartAction extends FlacAction {

	@Override
	public String execute() {
		getDownloadCartBean().getSelections().clear();
		return SUCCESS;
	}
}
