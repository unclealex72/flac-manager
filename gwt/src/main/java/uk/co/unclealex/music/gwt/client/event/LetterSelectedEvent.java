package uk.co.unclealex.music.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class LetterSelectedEvent extends GwtEvent<LetterSelectedEventHandler> {
  public static Type<LetterSelectedEventHandler> TYPE = new Type<LetterSelectedEventHandler>();
  
  private char i_letter;
  
  public LetterSelectedEvent(char letter) {
		super();
		i_letter = letter;
	}

	@Override
  public Type<LetterSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(LetterSelectedEventHandler handler) {
    handler.onLetterSelected(this);
  }

	public char getLetter() {
		return i_letter;
	}

	public void setLetter(char letter) {
		i_letter = letter;
	}

}
