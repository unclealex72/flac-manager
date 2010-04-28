package uk.co.unclealex.music.gwt.client.action;

public interface ActionVisitor {

	public void visit(AlbumSelectedAction albumSelectedAction);

	public void visit(ArtistSelectedAction artistSelectedAction);

	public void visit(ArtworkSelectedAction artworkSelectedAction);

	public void visit(LetterSelectedAction letterSelectedAction);

	public void visit(MissingCoversSelectedAction missingCoversSelectedAction);

	public void visit(SearchArtworkAction searchArtworkAction);

}
