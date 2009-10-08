package uk.co.unclealex.music.encoder.action;


public abstract class EncodingActionVisitor {

	public abstract void visit(AlbumCoverAddedAction albumCoverAddedAction);

	public abstract void visit(AlbumCoverRemovedAction albumCoverRemovedAction);

	public abstract void visit(FileAddedAction fileAddedAction);

	public abstract void visit(FileRemovedAction fileRemovedAction);

	public abstract void visit(TrackImportedAction trackImportedAction);

	public abstract void visit(TrackEncodedAction trackEncodedAction);

	public abstract void visit(TrackRemovedAction trackRemovedAction);

	public abstract void visit(AlbumAddedAction albumAddedAction);

	public abstract void visit(ArtistAddedAction artistAddedAction);

	public abstract void visit(AlbumRemovedAction albumRemovedAction);

	public abstract void visit(ArtistRemovedAction artistRemovedAction);

	public abstract void visit(TrackOwnedAction trackOwnedAction);

	public abstract void visit(TrackUnownedAction trackUnownedAction);

	public void visit(EncodingAction encodingAction) {
		throw new IllegalArgumentException(encodingAction.getClass() + " is not recognised.");
	}

}
