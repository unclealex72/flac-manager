package uk.co.unclealex.music.gwt.client.action;

import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;

public class AlbumSelectedAction extends Action {

	public static class Factory implements ActionFactory<AlbumInformationBean> {
		public Action createAction(AlbumInformationBean albumInformationBean) {
			return new AlbumSelectedAction(albumInformationBean);
		}
	}
	
	private AlbumInformationBean i_albumInformationBean;
	
	public AlbumSelectedAction() {
		super();
	}
	
	public AlbumSelectedAction(AlbumInformationBean albumInformationBean) {
		i_albumInformationBean = albumInformationBean;
	}

	@Override
	public void accept(ActionVisitor visitor) {
		visitor.visit(this);
	}

	public AlbumInformationBean getAlbumInformationBean() {
		return i_albumInformationBean;
	}

	public void setAlbumInformationBean(AlbumInformationBean albumInformationBean) {
		i_albumInformationBean = albumInformationBean;
	}

}
