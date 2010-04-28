package uk.co.unclealex.music.gwt.client.action;

import uk.co.unclealex.music.gwt.client.model.SearchInformationBean;

public class SearchArtworkAction extends Action {

	public static class Factory implements ActionFactory<SearchInformationBean> {
		public Action createAction(SearchInformationBean searchInformationBean) {
			return new SearchArtworkAction(searchInformationBean);
		}
	}

	private SearchInformationBean i_searchInformationBean;
		
	public SearchArtworkAction() {
		super();
	}
	
	public SearchArtworkAction(SearchInformationBean searchInformationBean) {
		super();
		i_searchInformationBean = searchInformationBean;
	}

	@Override
	public void accept(ActionVisitor visitor) {
		visitor.visit(this);
	}

	public SearchInformationBean getSearchInformationBean() {
		return i_searchInformationBean;
	}

	public void setSearchInformationBean(SearchInformationBean searchInformationBean) {
		i_searchInformationBean = searchInformationBean;
	}

}
