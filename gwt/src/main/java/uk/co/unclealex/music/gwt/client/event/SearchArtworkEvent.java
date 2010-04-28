package uk.co.unclealex.music.gwt.client.event;

import uk.co.unclealex.music.gwt.client.model.SearchInformationBean;

import com.google.gwt.event.shared.GwtEvent;

public class SearchArtworkEvent extends GwtEvent<SearchArtworkEventHandler> {
  public static Type<SearchArtworkEventHandler> TYPE = new Type<SearchArtworkEventHandler>();
  
  private SearchInformationBean i_searchInformationBean;
  
  public SearchArtworkEvent(SearchInformationBean searchInformationBean) {
		super();
		i_searchInformationBean = searchInformationBean;
	}

	@Override
  public Type<SearchArtworkEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(SearchArtworkEventHandler handler) {
    handler.onSearchArtwork(this);
  }

	public SearchInformationBean getSearchInformationBean() {
		return i_searchInformationBean;
	}

	public void setSearchInformationBean(SearchInformationBean searchInformationBean) {
		i_searchInformationBean = searchInformationBean;
	}
}
