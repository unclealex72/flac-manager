package uk.co.unclealex.music.core.service.filesystem;

public interface PathComponentFactory {

	public PathComponent createRootPathComponent(Context context);
	public PathComponent createEncoderPathComponent(Context context);
	public PathComponent createFirstLetterOfArtistPathComponent(Context context);
	public PathComponent createArtistPathComponent(Context context);
	public PathComponent createAlbumPathComponent(Context context);
	public PathComponent createTrackPathComponent(Context context);
}
