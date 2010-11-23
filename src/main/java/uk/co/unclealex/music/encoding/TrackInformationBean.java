package uk.co.unclealex.music.encoding;


public class TrackInformationBean implements Comparable<TrackInformationBean> {

	private String i_artist;
	private String i_album;
	private int i_trackNumber;
	private String i_title;
	private boolean i_compilation;
	
	public TrackInformationBean(String artist, String album, boolean compilation, int trackNumber, String title) {
		super();
		i_artist = artist;
		i_album = album;
		i_trackNumber = trackNumber;
		i_title = title;
		i_compilation = compilation;
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
					if (cmp == 0) {
						cmp = Boolean.valueOf(isCompilation()).compareTo(other.isCompilation());
					}
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
			(isCompilation() == other.isCompilation()) &&
			(getTitle().equals(other.getTitle()));
	}
	
	@Override
	public int hashCode() {
		Object[] objects = new Object[] { getArtist(), getAlbum(), getTrackNumber(), getTitle(), isCompilation()};
		int hashCode = 17;
		for (Object obj : objects) {
			int hash = obj.hashCode();
			hashCode = hashCode * 37 + hash;
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		String str = String.format("%s, %s: %02d - %s", getArtist(), getAlbum(), getTrackNumber(), getTitle());
		if (isCompilation()) {
			str += " (Compilation)";
		}
		return str;
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

	public boolean isCompilation() {
		return i_compilation;
	}
	
	
}
