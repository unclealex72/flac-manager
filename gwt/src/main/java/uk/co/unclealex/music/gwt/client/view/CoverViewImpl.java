package uk.co.unclealex.music.gwt.client.view;

import uk.co.unclealex.music.gwt.client.model.ArtworkInformationBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class CoverViewImpl extends Composite implements CoverView {

	@UiTemplate("CoverView.ui.xml")
  interface CoverViewUiBinder extends UiBinder<Widget, CoverViewImpl> {}
  private static CoverViewUiBinder uiBinder = GWT.create(CoverViewUiBinder.class);
  
  @UiField Image image;
  
  private Presenter presenter;
  
  public CoverViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void initialise(ArtworkInformationBean artworkInformationBean) {
  	image.setUrl(artworkInformationBean.getUrl());
  }
  
  @UiHandler("image")
  void onClicked(ClickEvent event) {
    if (presenter != null) {
      presenter.onClicked();
    }
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
}
