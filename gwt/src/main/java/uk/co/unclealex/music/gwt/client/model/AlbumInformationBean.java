package uk.co.unclealex.music.gwt.client.model;


public class AlbumInformationBean extends InformationBean<AlbumInformationBean> {

	private String i_album;
	private ArtistInformationBean i_artistInformationBean;
	
	public AlbumInformationBean() {
		super();
	}

	public AlbumInformationBean(ArtistInformationBean artistInformationBean, String album, String relativePath) {
		super(relativePath);
		i_album = album;
		i_artistInformationBean = artistInformationBean;
	}

	public int compareTo(AlbumInformationBean o) {
		ArtistInformationBean artistInformationBean = getArtistInformationBean();
		int cmp = artistInformationBean==null?0:artistInformationBean.compareTo(o.getArtistInformationBean()); 
		return cmp == 0?getAlbum().compareTo(o.getAlbum()):cmp;
	}
	
	@Override
	public String toString() {
		return getArtistInformationBean().getArtist() + ", " + getAlbum() + " (" + getUrl() + ")";
	}
	
	public String getAlbum() {
		return i_album;
	}

	public void setAlbum(String album) {
		i_album = album;
	}

	public ArtistInformationBean getArtistInformationBean() {
		return i_artistInformationBean;
	}

	public void setArtistInformationBean(ArtistInformationBean artistInformationBean) {
		i_artistInformationBean = artistInformationBean;
	}

}
