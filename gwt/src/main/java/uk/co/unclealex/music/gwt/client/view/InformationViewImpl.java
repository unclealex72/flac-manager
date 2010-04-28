package uk.co.unclealex.music.gwt.client.view;

import java.util.SortedMap;
import java.util.Map.Entry;

import uk.co.unclealex.music.gwt.client.model.InformationBean;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class InformationViewImpl<I extends InformationBean<I>> extends Composite implements InformationView<I> {

  @UiField VerticalPanel panel;

  private Presenter<I> presenter;
  private SortedMap<I, String> informationBeansAndTokens;
  
  public void showInformation() {
  	panel.clear();
  	for (Entry<I, String> entry : informationBeansAndTokens.entrySet()) {
  		I informationBean = entry.getKey();
			Widget informationWidget = createInformationWidget(informationBean, entry.getValue());
			panel.add(informationWidget);
  	}
  }

  protected abstract Widget createInformationWidget(I informationBean, String token);
  
  public Widget asWidget() {
    return this;
  }

	public Presenter<I> getPresenter() {
		return presenter;
	}


	public void setPresenter(Presenter<I> presenter) {
		this.presenter = presenter;
	}

	public SortedMap<I, String> getInformationBeansAndTokens() {
		return informationBeansAndTokens;
	}

	public void setInformationBeansAndTokens(SortedMap<I, String> informationBeansAndTokens) {
		this.informationBeansAndTokens = informationBeansAndTokens;
		showInformation();
	}
}
