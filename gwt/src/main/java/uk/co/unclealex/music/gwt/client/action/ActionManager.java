package uk.co.unclealex.music.gwt.client.action;

import uk.co.unclealex.music.gwt.client.FailureAwareAsyncCallback;
import uk.co.unclealex.music.gwt.client.GwtAlbumCoverServiceAsync;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ActionManager {

	private GwtAlbumCoverServiceAsync i_rpcService;
	
	public ActionManager(GwtAlbumCoverServiceAsync rpcService) {
		super();
		i_rpcService = rpcService;
	}

	public void addValueChangeHandler(ValueChangeHandler<String> handler) {
		History.addValueChangeHandler(handler);
	}
	
	public void fireLastAction(Action defaultAction) {
    if ("".equals(History.getToken())) {
      publishAction(defaultAction);
    }
    else {
    	History.fireCurrentHistoryState();
    }
	}
	
	public void publishAction(Action action) {
		AsyncCallback<String> callback = new FailureAwareAsyncCallback<String>() {
			public void onSuccess(String token) {
				History.newItem(token);
			}
		};
		getRpcService().serialise(action, callback);
	}

	public void accept(String token, final ActionVisitor visitor) {
		AsyncCallback<Action> callback = new FailureAwareAsyncCallback<Action>() {
			public void onSuccess(Action action) {
				action.accept(visitor);
			}
		};
		getRpcService().deserialise(token, callback);
	}
	public GwtAlbumCoverServiceAsync getRpcService() {
		return i_rpcService;
	}
}
