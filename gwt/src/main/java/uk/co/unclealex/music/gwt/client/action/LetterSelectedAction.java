package uk.co.unclealex.music.gwt.client.action;



public class LetterSelectedAction extends Action {

	public static class Factory implements ActionFactory<Character> {
		public Action createAction(Character ch) {
			return new LetterSelectedAction(ch);
		}
	}

	private char i_firstLetter;
	
	public LetterSelectedAction() {
		super();
	}

	public LetterSelectedAction(char firstLetter) {
		super();
		i_firstLetter = firstLetter;
	}

	@Override
	public void accept(ActionVisitor visitor) {
		visitor.visit(this);
	}

	public char getFirstLetter() {
		return i_firstLetter;
	}

	public void setFirstLetter(char firstLetter) {
		i_firstLetter = firstLetter;
	}
}
