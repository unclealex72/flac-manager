package uk.co.unclealex.music.encoder.action;


abstract class FileAction extends EncodingAction {

	private String i_path;
	
	public FileAction(String path) {
		super();
		i_path = path;
	}

	@Override
	public void accept(EncodingActionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public <E> E accept(ValueEncodingActionVisitor<E> visitor) {
		return visitor.visit(this);
	}

	public String getPath() {
		return i_path;
	}	
}
