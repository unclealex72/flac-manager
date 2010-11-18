package uk.co.unclealex.music.encoding;


public class TrackInformationBean implements Comparable<TrackInformationBean> {

	private String i_artist;
	private String i_album;
	private int i_trackNumber;
	private String i_title;
	
	public TrackInformationBean(String artist, String album, int trackNumber, String title) {
		super();
		i_artist = artist;
		i_album = album;
		i_trackNumber = trackNumber;
		i_title = title;
	}

	@Override
	public int compareTo(TrackInformationBean other) {
		int cmp = getArtist().compareTo(other.getArtist());
		if (cmp == 0) {
			cmp = getAlbum().compareTo(other.getAlbum());
			if (cmp == 0) {
				cmp = getTrackNumber() - other.getTrackNumber();
				if (cmp == 0) {
					cmp = getTitle().compareTo(other.getTitle());
				}
			}
		}
		return cmp;
	}
	
	@Override
	public boolean equals(Object obj) {
		TrackInformationBean other;
		return 
			(obj instanceof TrackInformationBean) &&
			(getArtist().equals((other = (TrackInformationBean) obj).getArtist())) &&
			(getAlbum().equals(other.getAlbum())) &&
			(getTrackNumber() == other.getTrackNumber()) &&
			(getTitle().equals(other.getTitle()));
	}
	
	@Override
	public int hashCode() {
		int[] hashes = new int[] { getArtist().hashCode(), getAlbum().hashCode(), getTrackNumber(), getTitle().hashCode() };
		int hashCode = 17;
		for (int hash : hashes) {
			hashCode = hashCode * 37 + hash;
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		return String.format("%s, %s: %02d - %s", getArtist(), getAlbum(), getTrackNumber(), getTitle());
	}
	
	public String getArtist() {
		return i_artist;
	}

	public String getAlbum() {
		return i_album;
	}

	public int getTrackNumber() {
		return i_trackNumber;
	}

	public String getTitle() {
		return i_title;
	}
	
	
}
