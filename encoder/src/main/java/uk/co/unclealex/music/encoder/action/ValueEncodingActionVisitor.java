package uk.co.unclealex.music.encoder.action;


public abstract class ValueEncodingActionVisitor<E> {

	public abstract E visit(AlbumCoverAddedAction albumCoverAddedAction);

	public abstract E visit(AlbumCoverRemovedAction albumCoverRemovedAction);

	public abstract E visit(FileAddedAction fileAddedAction);

	public abstract E visit(FileRemovedAction fileRemovedAction);

	public abstract E visit(TrackImportedAction trackImportedAction);

	public abstract E visit(TrackEncodedAction trackEncodedAction);

	public abstract E visit(TrackRemovedAction trackRemovedAction);

	public abstract E visit(AlbumAddedAction albumAddedAction);

	public abstract E visit(ArtistAddedAction artistAddedAction);

	public abstract E visit(AlbumRemovedAction albumRemovedAction);

	public abstract E visit(ArtistRemovedAction artistRemovedAction);

	public abstract E visit(TrackOwnedAction trackOwnedAction);

	public abstract E visit(TrackUnownedAction trackUnownedAction);

	public E visit(EncodingAction encodingAction) {
		throw new IllegalArgumentException(encodingAction.getClass() + " is not recognised.");
	}
}
