package uk.co.unclealex.music.web.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import uk.co.unclealex.music.core.model.EncodedBean;

public class DownloadCartBean implements Serializable {

	private List<EncodedBean> i_selections = new LinkedList<EncodedBean>();

	public List<EncodedBean> getSelections() {
		return i_selections;
	}

	public void setSelections(List<EncodedBean> selections) {
		i_selections = selections;
	}

	
}
