package uk.co.unclealex.music.gwt.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.Widget;

public class AlbumViewImpl extends Composite implements AlbumView {

	@UiTemplate("AlbumView.ui.xml")
  interface AlbumViewUiBinder extends UiBinder<Widget, AlbumViewImpl> {}
  private static AlbumViewUiBinder uiBinder = GWT.create(AlbumViewUiBinder.class);
  
  @UiField Image artworkImage;
  @UiField HasValue<String> artist;
  @UiField HasValue<String> album;
  @UiField SubmitButton searchButton;
  @UiField HasValue<String> url;
  @UiField SubmitButton urlButton;
  @UiField FileUpload fileUpload;

	private Presenter presenter;
  
  public AlbumViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void updateAlbumArtwork(String url) {
  	artworkImage.setUrl(url);
  }
  
  @UiHandler("searchButton")
  void onAddButtonClicked(ClickEvent event) {
    if (presenter != null) {
      presenter.onSearchClicked();
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

	public HasValue<String> getArtist() {
		return artist;
	}

	public HasValue<String> getAlbum() {
		return album;
	}

	public HasValue<String> getUrl() {
		return url;
	}
}
