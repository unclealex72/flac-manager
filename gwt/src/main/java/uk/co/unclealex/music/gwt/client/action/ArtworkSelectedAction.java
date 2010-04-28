package uk.co.unclealex.music.gwt.client.action;

import uk.co.unclealex.music.gwt.client.model.ArtworkInformationBean;

public class ArtworkSelectedAction extends Action {

	public static class Factory implements ActionFactory<ArtworkInformationBean> {
		public Action createAction(ArtworkInformationBean artworkInformationBean) {
			return new ArtworkSelectedAction(artworkInformationBean);
		}
	}

	private ArtworkInformationBean i_artworkInformationBean;
	
	public ArtworkSelectedAction() {
		super();
	}

	public ArtworkSelectedAction(ArtworkInformationBean artworkInformationBean) {
		super();
		i_artworkInformationBean = artworkInformationBean;
	}

	@Override
	public void accept(ActionVisitor visitor) {
		visitor.visit(this);
	}

	public ArtworkInformationBean getArtworkInformationBean() {
		return i_artworkInformationBean;
	}

	public void setArtworkInformationBean(ArtworkInformationBean artworkInformationBean) {
		i_artworkInformationBean = artworkInformationBean;
	}

}
