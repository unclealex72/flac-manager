package uk.co.unclealex.flacconverter.actions;

public class LetterAwareAction extends FlacAction {

	private char i_letter;

	public char getLetter() {
		return i_letter;
	}

	public void setLetter(char letter) {
		i_letter = letter;
	}
	
}
