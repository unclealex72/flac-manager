package uk.co.unclealex.music.gwt.client.action;


public class MissingCoversSelectedAction extends Action {

	public static class Factory implements ActionFactory<Object> {
		public Action createAction(Object dummy) {
			return new MissingCoversSelectedAction();
		}
	}

	@Override
	public void accept(ActionVisitor visitor) {
		visitor.visit(this);
	}
}
