package uk.co.unclealex.flacconverter.flac.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class DownloadCartBean implements Serializable {

	private List<FlacBean> i_selections = new LinkedList<FlacBean>();

	public List<FlacBean> getSelections() {
		return i_selections;
	}

	public void setSelections(List<FlacBean> selections) {
		i_selections = selections;
	}

	
}
