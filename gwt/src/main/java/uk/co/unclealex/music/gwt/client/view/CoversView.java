package uk.co.unclealex.music.gwt.client.view;

import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public interface CoversView {

	public interface Presenter {
		
		public void initialise(AlbumInformationBean albumInformationBean);		
	}
	
  public void setPresenter(Presenter presenter);

	public Widget asWidget();
	
	public HasWidgets asContainer();
}
