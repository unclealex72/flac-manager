package uk.co.unclealex.music.gwt.client.view;

import uk.co.unclealex.music.gwt.client.model.AlbumInformationBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class AlbumsViewImpl extends InformationViewImpl<AlbumInformationBean> implements AlbumsView {

	@SuppressWarnings("unchecked")
	@UiTemplate("AlbumsView.ui.xml")
  interface AlbumsViewUiBinder extends UiBinder<Widget, InformationViewImpl> {}
  private static AlbumsViewUiBinder uiBinder = 
    GWT.create(AlbumsViewUiBinder.class);

  public AlbumsViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
	@Override
	protected Widget createInformationWidget(AlbumInformationBean informationBean, String token) {
		HorizontalPanel panel = new HorizontalPanel();
		Image image = new Image();
		getPresenter().setImageUrl(image, informationBean);
		Hyperlink hyperlink = 
			new Hyperlink(informationBean.getArtistInformationBean().getArtist() + ": " + informationBean.getAlbum(), token);
		panel.add(image);
		panel.add(hyperlink);
		return panel;
	}

}
