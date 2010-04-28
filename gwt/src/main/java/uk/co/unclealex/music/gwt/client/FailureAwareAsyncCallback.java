package uk.co.unclealex.music.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class FailureAwareAsyncCallback<T> implements AsyncCallback<T> {

	public void onFailure(Throwable t) {
		Fail.fail(t);
	}
}
