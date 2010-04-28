package uk.co.unclealex.music.gwt.client.view;

import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public class ArtistsViewImpl extends InformationViewImpl<ArtistInformationBean> implements ArtistsView {

	@SuppressWarnings("unchecked")
	@UiTemplate("ArtistsView.ui.xml")
  interface AlbumsViewUiBinder extends UiBinder<Widget, InformationViewImpl> {}
  private static AlbumsViewUiBinder uiBinder = 
    GWT.create(AlbumsViewUiBinder.class);

  public ArtistsViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  

	@Override
	protected Widget createInformationWidget(ArtistInformationBean informationBean, String token) {
		return new Hyperlink(informationBean.getArtist(), token);
	}

}
