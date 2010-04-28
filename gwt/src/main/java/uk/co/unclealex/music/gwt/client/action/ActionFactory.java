package uk.co.unclealex.music.gwt.client.action;

import java.io.Serializable;


public interface ActionFactory<T> extends Serializable {

	public Action createAction(T parameter);
}
