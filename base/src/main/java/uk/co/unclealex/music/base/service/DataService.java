package uk.co.unclealex.music.base.service;

import java.io.IOException;

import uk.co.unclealex.hibernate.model.DataBean;

public interface DataService {
	
	public DataBean createDataBean() throws IOException;	
}
