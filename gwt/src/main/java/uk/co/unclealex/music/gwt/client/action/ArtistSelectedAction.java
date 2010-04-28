package uk.co.unclealex.music.gwt.client.action;

import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;

public class ArtistSelectedAction extends Action {

	public static class Factory implements ActionFactory<ArtistInformationBean> {
		public Action createAction(ArtistInformationBean artistInformationBean) {
			return new ArtistSelectedAction(artistInformationBean);
		}
	}

	private ArtistInformationBean i_artistInformationBean;
	
	public ArtistSelectedAction() {
		super();
	}

	public ArtistSelectedAction(ArtistInformationBean artistInformationBean) {
		super();
		i_artistInformationBean = artistInformationBean;
	}

	@Override
	public void accept(ActionVisitor visitor) {
		visitor.visit(this);
	}

	public ArtistInformationBean getArtistInformationBean() {
		return i_artistInformationBean;
	}

	public void setArtistInformationBean(ArtistInformationBean artistInformationBean) {
		i_artistInformationBean = artistInformationBean;
	}

}
