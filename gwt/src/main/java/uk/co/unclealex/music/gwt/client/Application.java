package uk.co.unclealex.music.gwt.client;

import uk.co.unclealex.music.gwt.client.action.ActionManager;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

public class Application implements EntryPoint {

  public void onModuleLoad() {
    GwtAlbumCoverServiceAsync rpcService = GwtAlbumCoverServiceAsync.Util.getInstance();
    HandlerManager eventBus = new HandlerManager(null);
    ActionManager actionManager = new ActionManager(rpcService);
    AppController appViewer = new AppController(rpcService, actionManager, eventBus);
    appViewer.go(RootPanel.get());
  }
}
