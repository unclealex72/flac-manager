package uk.co.unclealex.music.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class Fail {

	public static void fail(Throwable t) {
		GWT.log("An server side error occurred", t);
		Window.alert(t.getClass() + ": " + t.getMessage());
	}

}
