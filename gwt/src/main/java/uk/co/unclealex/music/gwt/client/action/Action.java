package uk.co.unclealex.music.gwt.client.action;

import java.io.Serializable;

public abstract class Action implements Serializable {

	public abstract void accept(ActionVisitor visitor);
}
