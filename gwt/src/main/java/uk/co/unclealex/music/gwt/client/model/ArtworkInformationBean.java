package uk.co.unclealex.music.gwt.client.model;


public class ArtworkInformationBean extends InformationBean<ArtworkInformationBean> {

	private AlbumInformationBean i_albumInformationBean;
	
	public ArtworkInformationBean() {
		super();
	}

	public ArtworkInformationBean(AlbumInformationBean albumInformationBean, String url) {
		super(url);
		i_albumInformationBean = albumInformationBean;
	}

	public int compareTo(ArtworkInformationBean o) {
		int cmp = getAlbumInformationBean().compareTo(o.getAlbumInformationBean());
		return cmp == 0?super.compareTo(o):cmp;
	}
	
	@Override
	public String toString() {
		AlbumInformationBean albumInformationBean = getAlbumInformationBean();
		return albumInformationBean.getArtistInformationBean().getArtist() + ", " + albumInformationBean.getAlbum() + " (" + getUrl() + ")";
	}

	public AlbumInformationBean getAlbumInformationBean() {
		return i_albumInformationBean;
	}

	public void setAlbumInformationBean(AlbumInformationBean albumInformationBean) {
		i_albumInformationBean = albumInformationBean;
	}
	
}
