package uk.co.unclealex.music.gwt.client.view;

import java.util.SortedMap;

import uk.co.unclealex.music.gwt.client.model.InformationBean;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public interface InformationView<I extends InformationBean<I>> {

  public interface Presenter<I> {
    public void setImageUrl(Image image, I informationBean);
  }
  
  public void setPresenter(Presenter<I> presenter);
  public void setInformationBeansAndTokens(SortedMap<I, String> informationBeansAndTokens);

  Widget asWidget();
}
