package uk.co.unclealex.music.gwt.client.view;

import java.util.SortedMap;
import java.util.Map.Entry;

import uk.co.unclealex.music.gwt.client.model.ArtistInformationBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class NavigationViewImpl extends Composite implements NavigationView {

	@UiTemplate("NavigationView.ui.xml")
  interface NavigationViewUiBinder extends UiBinder<Widget, NavigationViewImpl> {}
  private static NavigationViewUiBinder uiBinder = GWT.create(NavigationViewUiBinder.class);
  
  @UiField Hyperlink missingCoversHyperlink;
  @UiField Hyperlink artistHyperlink;
  @UiField Panel lettersPanel;
  
  private Presenter presenter;
  private SortedMap<Character, String> firstLetters;
  
  public NavigationViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void hideArtist() {
  	artistHyperlink.setVisible(false);
  }
  
  public void showArtist(ArtistInformationBean artistInformationBean, String token) {
  	artistHyperlink.setText(artistInformationBean.getArtist());
  	artistHyperlink.setTargetHistoryToken(token);
  	artistHyperlink.setVisible(true);
  }
  
  public void initialise() {
  	if (firstLetters != null) {
	  	lettersPanel.clear();
	  	for (Entry<Character, String> entry : firstLetters.entrySet()) {
	  		Character firstLetter = entry.getKey();
				Hyperlink hyperlink = 
	  			new Hyperlink(String.valueOf(Character.toUpperCase(firstLetter)), entry.getValue());
				lettersPanel.add(hyperlink);
	  	}
  	}
  }

	public Widget asWidget() {
    return this;
  }

	public SortedMap<Character, String> getFirstLetters() {
		return firstLetters;
	}

	public void setFirstLetters(SortedMap<Character, String> firstLetters) {
		this.firstLetters = firstLetters;
		initialise();
	}

	public Presenter getPresenter() {
		return presenter;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
