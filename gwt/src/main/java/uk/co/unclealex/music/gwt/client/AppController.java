package uk.co.unclealex.music.gwt.client;

import uk.co.unclealex.music.gwt.client.action.ActionManager;
import uk.co.unclealex.music.gwt.client.action.ActionVisitor;
import uk.co.unclealex.music.gwt.client.action.AlbumSelectedAction;
import uk.co.unclealex.music.gwt.client.action.ArtistSelectedAction;
import uk.co.unclealex.music.gwt.client.action.ArtworkSelectedAction;
import uk.co.unclealex.music.gwt.client.action.LetterSelectedAction;
import uk.co.unclealex.music.gwt.client.action.MissingCoversSelectedAction;
import uk.co.unclealex.music.gwt.client.action.SearchArtworkAction;
import uk.co.unclealex.music.gwt.client.event.AlbumSelectedEvent;
import uk.co.unclealex.music.gwt.client.event.AlbumSelectedEventHandler;
import uk.co.unclealex.music.gwt.client.event.ArtistSelectedEvent;
import uk.co.unclealex.music.gwt.client.event.ArtistSelectedEventHandler;
import uk.co.unclealex.music.gwt.client.event.ArtworkSelectedEvent;
import uk.co.unclealex.music.gwt.client.event.ArtworkSelectedEventHandler;
import uk.co.unclealex.music.gwt.client.event.LetterSelectedEvent;
import uk.co.unclealex.music.gwt.client.event.LetterSelectedEventHandler;
import uk.co.unclealex.music.gwt.client.event.MissingCoversSelectedEvent;
import uk.co.unclealex.music.gwt.client.event.MissingCoversSelectedEventHandler;
import uk.co.unclealex.music.gwt.client.event.SearchArtworkEvent;
import uk.co.unclealex.music.gwt.client.event.SearchArtworkEventHandler;
import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;
import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;
import uk.co.unclealex.music.gwt.client.presenter.AlbumPresenter;
import uk.co.unclealex.music.gwt.client.presenter.AlbumsPresenter;
import uk.co.unclealex.music.gwt.client.presenter.ArtistsPresenter;
import uk.co.unclealex.music.gwt.client.presenter.CoversPresenter;
import uk.co.unclealex.music.gwt.client.presenter.MissingCoversPresenter;
import uk.co.unclealex.music.gwt.client.presenter.NavigationPresenter;
import uk.co.unclealex.music.gwt.client.presenter.Presenter;
import uk.co.unclealex.music.gwt.client.view.AlbumViewImpl;
import uk.co.unclealex.music.gwt.client.view.AlbumsViewImpl;
import uk.co.unclealex.music.gwt.client.view.ArtistsViewImpl;
import uk.co.unclealex.music.gwt.client.view.CoversViewImpl;
import uk.co.unclealex.music.gwt.client.view.NavigationViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class AppController extends Composite implements Presenter, ValueChangeHandler<String>, ActionVisitor {
	
	@UiTemplate("AppController.ui.xml")
  interface AppControllerUiBinder extends UiBinder<Widget, AppController> {}
  private static AppControllerUiBinder uiBinder = GWT.create(AppControllerUiBinder.class);

  private final HandlerManager eventBus;
  private final GwtAlbumCoverServiceAsync rpcService; 
  private ActionManager actionManager;
  private NavigationPresenter navigationPresenter;
  
  @UiField Panel navigationPanel;
  @UiField Panel applicationPanel;
  
  public AppController(GwtAlbumCoverServiceAsync rpcService, ActionManager actionManager, HandlerManager eventBus) {
    this.eventBus = eventBus;
    this.rpcService = rpcService;
    this.actionManager = actionManager;
    bind();
  }
  
  private void bind() {
  	actionManager.addValueChangeHandler(this);

    eventBus.addHandler(AlbumSelectedEvent.TYPE,
        new AlbumSelectedEventHandler() {
          public void onAlbumSelected(AlbumSelectedEvent event) {
            actionManager.publishAction(new AlbumSelectedAction(event.getAlbumInformationBean()));
          }
        });  

    eventBus.addHandler(ArtistSelectedEvent.TYPE,
        new ArtistSelectedEventHandler() {
          public void onArtistSelected(ArtistSelectedEvent event) {
            actionManager.publishAction(new ArtistSelectedAction(event.getArtistInformationBean()));
          }
        });  

    eventBus.addHandler(ArtworkSelectedEvent.TYPE,
        new ArtworkSelectedEventHandler() {
          public void onArtworkSelected(ArtworkSelectedEvent event) {
            actionManager.publishAction(new ArtworkSelectedAction(event.getArtworkInformationBean()));
          }
        });  

    eventBus.addHandler(LetterSelectedEvent.TYPE,
        new LetterSelectedEventHandler() {
          public void onLetterSelected(LetterSelectedEvent event) {
            actionManager.publishAction(new LetterSelectedAction(event.getLetter()));
          }
        });  

    eventBus.addHandler(MissingCoversSelectedEvent.TYPE,
        new MissingCoversSelectedEventHandler() {
          public void onMissingCoversSelected(MissingCoversSelectedEvent event) {
            actionManager.publishAction(new MissingCoversSelectedAction());
          }
        });  

    eventBus.addHandler(SearchArtworkEvent.TYPE,
        new SearchArtworkEventHandler() {
					public void onSearchArtwork(SearchArtworkEvent event) {
            actionManager.publishAction(new SearchArtworkAction(event.getSearchInformationBean()));
					}
				});  

    initWidget(uiBinder.createAndBindUi(this));
    navigationPresenter = new NavigationPresenter(rpcService, eventBus, new NavigationViewImpl());
    navigationPresenter.go(navigationPanel);
  }
  
  public void visit(MissingCoversSelectedAction missingCoversSelectedAction) {
  	new MissingCoversPresenter(rpcService, eventBus, new AlbumsViewImpl()).go(applicationPanel);
  	navigationPresenter.hideArtist();
  }

  public void visit(ArtistSelectedAction artistSelectedAction) {
  	ArtistInformationBean artistInformationBean = artistSelectedAction.getArtistInformationBean();
		new AlbumsPresenter(rpcService, eventBus, new AlbumsViewImpl(), artistInformationBean).go(applicationPanel);
  	navigationPresenter.showArtist(artistInformationBean);
  }

  public void visit(LetterSelectedAction letterSelectedAction) {
  	new ArtistsPresenter(rpcService, eventBus, new ArtistsViewImpl(), letterSelectedAction.getFirstLetter()).go(applicationPanel);
  	navigationPresenter.hideArtist();
  }
  
  public void visit(AlbumSelectedAction albumSelectedAction) {
		AlbumInformationBean albumInformationBean = albumSelectedAction.getAlbumInformationBean();
		new AlbumPresenter(rpcService, eventBus, new AlbumViewImpl(), albumInformationBean).go(applicationPanel);
  	navigationPresenter.showArtist(albumInformationBean.getArtistInformationBean());
  }
  
  public void visit(ArtworkSelectedAction artworkSelectedAction) {
  	AlbumInformationBean albumInformationBean = artworkSelectedAction.getArtworkInformationBean().getAlbumInformationBean();
  	navigationPresenter.showArtist(albumInformationBean.getArtistInformationBean());
		Window.alert("Selected artwork " + albumInformationBean);  	
  }
  
  public void visit(SearchArtworkAction searchArtworkAction) {
  	AlbumInformationBean albumInformationBean = searchArtworkAction.getSearchInformationBean().getAlbumInformationBean();
  	navigationPresenter.showArtist(albumInformationBean.getArtistInformationBean());
		CoversPresenter coversPresenter = new CoversPresenter(rpcService, eventBus, new CoversViewImpl());
		coversPresenter.go(applicationPanel);
		coversPresenter.initialise(albumInformationBean);
  }

  public void go(final HasWidgets container) {
    container.clear();
    container.add(this);
    new NavigationPresenter(rpcService, eventBus, new NavigationViewImpl()).go(navigationPanel);
    actionManager.fireLastAction(new MissingCoversSelectedAction());
  }

  public void onValueChange(ValueChangeEvent<String> event) {
    String token = event.getValue();
    actionManager.accept(token, this);
  }

	public ActionManager getActionManager() {
		return actionManager;
	}

	public void setActionManager(ActionManager actionManager) {
		this.actionManager = actionManager;
	} 
}
