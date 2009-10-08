package uk.co.unclealex.music.encoder.action;

public class DefaultValueEncodingActionVisitor<E> extends ValueEncodingActionVisitor<E> {

	private E i_defaultValue;

	public DefaultValueEncodingActionVisitor(E defaultValue) {
		super();
		i_defaultValue = defaultValue;
	}

	@Override
	public E visit(AlbumCoverAddedAction albumCoverAddedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(AlbumCoverRemovedAction albumCoverRemovedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(FileAddedAction fileAddedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(FileRemovedAction fileRemovedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(TrackImportedAction trackImportedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(TrackEncodedAction trackEncodedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(TrackRemovedAction trackRemovedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(AlbumAddedAction albumAddedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(ArtistAddedAction artistAddedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(AlbumRemovedAction albumRemovedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(ArtistRemovedAction artistRemovedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(TrackOwnedAction trackOwnedAction) {
		return getDefaultValue();
	}

	@Override
	public E visit(TrackUnownedAction trackUnownedAction) {
		return getDefaultValue();
	}
	
	public E getDefaultValue() {
		return i_defaultValue;
	}
}
