package uk.co.unclealex.music.gwt.client.view;

import uk.co.unclealex.music.gwt.client.model.ArtworkInformationBean;

import com.google.gwt.user.client.ui.Widget;

public interface CoverView {

	public interface Presenter {
		public void onClicked();
		public void initialise(ArtworkInformationBean artworkInformationBean);
	}
	
  public void setPresenter(Presenter presenter);

	public void initialise(ArtworkInformationBean artworkInformationBean);

	public Widget asWidget();
}
