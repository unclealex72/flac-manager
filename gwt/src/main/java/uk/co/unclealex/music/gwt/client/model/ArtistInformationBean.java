package uk.co.unclealex.music.gwt.client.model;


public class ArtistInformationBean extends InformationBean<ArtistInformationBean> {

	private String i_artist;
	
	public ArtistInformationBean() {
		super();
	}

	public ArtistInformationBean(String artist, String relativePath) {
		super(relativePath);
		i_artist = artist;
	}
	
	protected String removeArticle() {
		String artist = getArtist();
		return artist.startsWith("The ")?artist.substring(4):artist;
	}

	public int compareTo(ArtistInformationBean o) {
		return removeArticle().compareTo(o.removeArticle());
	}
	
	@Override
	public String toString() {
		return getArtist() + " (" + getUrl() + ")";
	}

	public String getArtist() {
		return i_artist;
	}

	public void setArtist(String artist) {
		i_artist = artist;
	}
}
