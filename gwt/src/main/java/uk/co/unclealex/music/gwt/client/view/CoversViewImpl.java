package uk.co.unclealex.music.gwt.client.view;

import uk.co.unclealex.music.gwt.client.model.ArtworkInformationBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class CoversViewImpl extends Composite implements CoversView {

	@UiTemplate("CoversView.ui.xml")
  interface CoversViewUiBinder extends UiBinder<Widget, CoversViewImpl> {}
  private static CoversViewUiBinder uiBinder = GWT.create(CoversViewUiBinder.class);
  
  @UiField HasWidgets panel;
  
  private Presenter presenter;
  private ArtworkInformationBean artworkInformationBean;
  
  public CoversViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public HasWidgets asContainer() {
  	return panel;
  }
  
  public Widget asWidget() {
  	return this;
  }
  
	public Presenter getPresenter() {
		return presenter;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public ArtworkInformationBean getArtworkInformationBean() {
		return artworkInformationBean;
	}

	public void setArtworkInformationBean(ArtworkInformationBean artworkInformationBean) {
		this.artworkInformationBean = artworkInformationBean;
	}

}
