package uk.co.unclealex.music.encoder.action;

public class FileAddedAction extends FileAction {

	public FileAddedAction(String path) {
		super(path);
	}

	@Override
	public void accept(EncodingActionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <E> E accept(ValueEncodingActionVisitor<E> visitor) {
		return visitor.visit(this);
	}
}
